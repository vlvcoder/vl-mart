package com.antonov.vlmart.shell;

import com.antonov.vlmart.model.enums.GOOD;
import com.antonov.vlmart.service.training.MultiplyTraining;
import com.antonov.vlmart.service.training.Training;
import com.antonov.vlmart.service.training.TrainingSet;
import com.antonov.vlmart.utils.AnsiStringBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.component.flow.ComponentFlow;
import org.springframework.shell.component.support.SelectorItem;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Scanner;
import java.util.stream.Collectors;

@ShellComponent
public class TrainingCommands {
    private final Scanner scanner = new Scanner(System.in);

    @Autowired
    private ComponentFlow.Builder componentFlowBuilder;

    @ShellMethod(value = "Логотип multiply")
    public void multiply_logo() {
        try {
            Files.lines(Path.of("banners/banner_multiply.txt")).forEach(System.out::println);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @ShellMethod(value = "Таблица Пифагора")
    public String pifagor() {
        var builder = new AnsiStringBuilder();
        new MultiplyTraining().pifagorTable(builder);
        return builder.toString();
    }

    @ShellMethod(value = "Тренажер таблицы умножения")
    public String multi() {
        System.out.println("Поехали ) \n");
        System.out.print("Выберите тип упражнений: ");
        TrainingSet.TRAINING_TYPE trainingType = selectTrainingType();
        System.out.print("Введите количество упражнений: ");
        int n = Integer.parseInt(scanner.nextLine());

        var trainingSet = new TrainingSet()
                .top(n)
                .type(trainingType);
        Training trainer = trainingSet.next();

        String question = trainer.question();
        while (true) {
            System.out.print(question);
            String input = scanner.nextLine();
            if ("exit".equalsIgnoreCase(input)) {
                break;
            }
            else if ("pif".equalsIgnoreCase(input)) {
                System.out.println(new MultiplyTraining().pifagorTable(new AnsiStringBuilder()));
            }
            else if (trainer.checkInput(input)) {
                System.out.print(trainer.answer(input));
                trainer = trainingSet.next();
                if (trainer == null) {
                    break;
                }
                question = trainer.question();
            }
            else {
                System.out.println("Не понял вас. Введите еще раз");
            }
        }
        return "До свидания!";
    }

    private TrainingSet.TRAINING_TYPE selectTrainingType() {
        var SELECTOR_NAME = "trainingType";
        var typeMap = Arrays
                .stream(TrainingSet.TRAINING_TYPE.values())
                .collect(Collectors.toMap(
                        TrainingSet.TRAINING_TYPE::title,
                        TrainingSet.TRAINING_TYPE::toString));

        ComponentFlow flow = componentFlowBuilder.clone().reset()
                .withSingleItemSelector(SELECTOR_NAME)
                .name("Выберите тип упражнений")
                .selectItems(typeMap)
                .and()
                .build();
        var result = flow.run();
        var res = result.getContext().get(SELECTOR_NAME, String.class);
        return Enum.valueOf(TrainingSet.TRAINING_TYPE.class, res);
    }

}
