package com.antonov.vlmart.shell;

import com.antonov.vlmart.service.training.MultiplyTraining;
import com.antonov.vlmart.utils.AnsiStringBuilder;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import java.util.Scanner;

import static com.antonov.vlmart.utils.AnsiStringBuilder.ForegroundColor.*;

@ShellComponent
public class FunCommands {

    final int FLAG_LENGTH = 36;
    AnsiStringBuilder builder = new AnsiStringBuilder();


    private final Scanner scanner = new Scanner(System.in);

    @ShellMethod(value = "Таблица Пифагора")
    public String funpif() {
        var builder = new AnsiStringBuilder();
        new MultiplyTraining().pifagorTable(builder);
        return builder.toString();
    }

    @ShellMethod(value = "Привет!")
    public String hi() {
        var builder = new AnsiStringBuilder();
        builder
                .color(AnsiStringBuilder.ForegroundColor.RED)
//                .reset()
                .line()
                .appendLine("Привет!")
                .line();

        return builder.toString();
    }

    @ShellMethod(value = "Российский флаг")
    public String ruflag() {
        color(WHITE);
        printLn();
        printLn();
        printLn();
        color(BLUE);
        printLn();
        printLn();
        printLn();
        color(RED);
        printLn();
        printLn();
        printLn();
        return builder.toString();
    }

    private void print()
    {
        String res = "";
        for (int i = 0; i < FLAG_LENGTH; i++) {
            builder.append("=");
        }
    }

    private void printLn()
    {
        print();
        builder.line();
    }



    private void color(AnsiStringBuilder.ForegroundColor col) {
        builder.color(col);
    }

}
