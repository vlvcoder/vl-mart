package com.antonov.vlmart.controller;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("/files")
@AllArgsConstructor
public class FileController {

    private static final Path CHUNK_DIR = Paths.get("/upload/chunks");
    private static final Path FINAL_DIR = Paths.get("/upload/final");
    private static final Path DOWNLOADING_FILE = Paths.get("/upload/myphoto-6.jpeg");

    private static final Path UPLOAD_DIR = Paths.get("/upload");

    /**
     * Со времён Chrome 105 можно передать ReadableStream как тело запроса с опцией duplex: 'half'.
     * Но браузеры требуют HTTP/2 или новее — на HTTP/1.1 будет ошибка net::ERR_H2_OR_QUIC_REQUIRED, потому что fetch не умеет выставлять Transfer-Encoding: chunked (заголовок под контролем браузера) и не даёт Content-Length для стрима.
     * stackoverflow.com +1
     * Полноценный двунаправленный стрим (duplex: 'full') в стабильных браузерах пока не появился — доступен только экспериментально.
     * @param request
     * @param filename
     * @return
     * @throws IOException
     */
    @PostMapping(value = "/upload-duplex", consumes = "application/octet-stream")
    public ResponseEntity<?> uploadDuplexStream(@NotNull HttpServletRequest request,
                                                @RequestHeader("X-Filename") String filename) throws IOException {

        Files.createDirectories(UPLOAD_DIR);
        Path target = UPLOAD_DIR.resolve(filename);

        // Прямая передача потока: request.getInputStream() -> файл на диске
        try (InputStream in = request.getInputStream();
             OutputStream out = Files.newOutputStream(target)) {
            in.transferTo(out); // Ничего не буферизуем в памяти
        }

        return ResponseEntity.ok(Map.of("status", "ok", "size", Files.size(target)));
    }

    @PostMapping("/upload-chunk")
    public ResponseEntity<?> uploadChunk(@RequestParam("chunk") MultipartFile chunk,
                                         @RequestParam("fileId") String fileId,
                                         @RequestParam("chunkIndex") int chunkIndex,
                                         @RequestParam("totalChunks") int totalChunks) throws IOException {
        Files.createDirectories(CHUNK_DIR.resolve(fileId));
        Path chunkPath = CHUNK_DIR.resolve(fileId).resolve(String.valueOf(chunkIndex));

        // Сохраняем чанк на диск (MultipartFile.getInputStream() тоже стримится)
        try (InputStream in = chunk.getInputStream()) {
            Files.copy(in, chunkPath, StandardCopyOption.REPLACE_EXISTING);
        }

        return ResponseEntity.ok(Map.of("chunk", chunkIndex, "status", "ok"));
    }

    @PostMapping("/upload-merge")
    public ResponseEntity<?> mergeChunks(@RequestBody MergeRequest req) throws IOException {
        Path chunkDir = CHUNK_DIR.resolve(req.getFileId());
        Files.createDirectories(FINAL_DIR);
        Path finalFile = FINAL_DIR.resolve(req.getFilename());

        try (OutputStream out = Files.newOutputStream(finalFile)) {
            for (int i = 0; i < req.getTotalChunks(); i++) {
                Path chunk = chunkDir.resolve(String.valueOf(i));
                // Копируем каждый чанк в итоговый файл последовательно
                Files.copy(chunk, out);
                Files.delete(chunk);
            }
        }
        Files.delete(chunkDir);

        return ResponseEntity.ok(Map.of("status", "merged", "file", finalFile.toString()));
    }

    @GetMapping("/download")
    public ResponseEntity<Resource> downloadFile() throws IOException {
        Resource resource = new FileSystemResource(DOWNLOADING_FILE);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + DOWNLOADING_FILE.getFileName() + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(Files.size(DOWNLOADING_FILE))
                .body(resource);
    }

    // DTO
    @Getter
    public static class MergeRequest {
        private String fileId;
        private int totalChunks;
        private String filename;
    }

}
