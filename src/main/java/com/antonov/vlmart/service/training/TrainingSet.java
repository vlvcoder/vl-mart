package com.antonov.vlmart.service.training;

import com.antonov.vlmart.utils.AnsiStringBuilder;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Accessors(fluent = true)
public class TrainingSet {

    private int top;
    public TrainingSet top(int top) {
        this.top = top;
        return this;
    }

    private TRAINING_TYPE type;
    public TrainingSet type(TRAINING_TYPE type) {
        this.type = type;
        list.clear();
        switch (type) {
            case MULTIPLY:
                list.add(new MultiplyTraining());
                break;
            case ADD:
                list.add(new EquationAddFirst());
                list.add(new EquationAddSecond());
                break;
            case SUB:
                list.add(new EquationSubFirst());
                list.add(new EquationSubSecond());
                break;
            case MULTI:
                list.add(new EquationMultiFirst());
                list.add(new EquationMultiSecond());
                break;
            case DIVIDE:
                list.add(new EquationDivideFirst());
                list.add(new EquationDivideSecond());
                break;
            case ADD_SUB:
                list.add(new EquationAddFirst());
                list.add(new EquationAddSecond());
                list.add(new EquationSubFirst());
                list.add(new EquationSubSecond());
                break;
            case MULTI_DIVIDE:
                list.add(new EquationMultiFirst());
                list.add(new EquationMultiSecond());
                list.add(new EquationDivideFirst());
                list.add(new EquationDivideSecond());
                break;
            case ALL_EQUATION:
                list.add(new EquationAddFirst());
                list.add(new EquationAddSecond());
                list.add(new EquationSubFirst());
                list.add(new EquationSubSecond());
                list.add(new EquationMultiFirst());
                list.add(new EquationMultiSecond());
                list.add(new EquationDivideFirst());
                list.add(new EquationDivideSecond());
                break;
            case ALL:
                list.add(new MultiplyTraining());
                list.add(new EquationAddFirst());
                list.add(new EquationAddSecond());
                list.add(new EquationSubFirst());
                list.add(new EquationSubSecond());
                list.add(new EquationMultiFirst());
                list.add(new EquationMultiSecond());
                list.add(new EquationDivideFirst());
                list.add(new EquationDivideSecond());
                break;
        }
        return this;
    }

    @Getter
    @Accessors(fluent = true)
    public enum TRAINING_TYPE {
        MULTIPLY("Таблица умножения"),
        ADD("Простые уравнения на сложение"),
        SUB("Простые уравнения на вычитание"),
        MULTI("Простые уравнения на умножение"),
        DIVIDE("Простые уравнения на деление"),
        ADD_SUB("Простые уравнения на сложение и вычитание"),
        MULTI_DIVIDE("Простые уравнения на умножение и деление"),
        ALL_EQUATION("Простые уравнения"),
        ALL("Простые уравнения и таблица умножения");

        private final String title;

        TRAINING_TYPE(String title) {
            this.title = title;
        }
    }

    private final List<Training> list = new ArrayList<>();
    private final Random random = new Random();
    private Training current;

    public Training next() {
        if (current == null) {
            current = list. get(0);
        }
        else {
            list.forEach(t -> t.copyScore(current));
            current = list.get((int) ((list.size()) * random.nextDouble()));
        }
        if (current.successCount >= top) {
            showGreat();
            return null;
        }
        return current;
    }

    private void showGreat() {
        var builder = new AnsiStringBuilder();
        builder
                .line()
                .line()
                .color(AnsiStringBuilder.ForegroundColor.LIGHT_GREEN);
        try {
            Files
                    .lines(Path.of("banners/banner_great.txt"))
                    .forEach(builder::appendLine);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        builder
                .line()
                .line()
                .reset();
        current.score(builder);
        System.out.println(builder);
    }

}
