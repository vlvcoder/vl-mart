package com.antonov.vlmart.service.training;

import com.antonov.vlmart.utils.AnsiStringBuilder;
import com.antonov.vlmart.utils.MatchParser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public abstract class Training {
    protected int count;
    protected int successCount;
    protected int failCount;

    protected String EXAMPLE_FORMAT = "%s * %s = %s";
    protected final Random random = new Random();

    protected List<Integer> numbers_1;
    protected List<Integer> numbers_2;

    protected int a;
    protected int b;

    protected int rangeLow = 2;
    protected int rangeHigh = 9;

    protected INPUT_TYPE inputType = INPUT_TYPE.FORMULA;

    protected boolean progressive;

    protected enum INPUT_TYPE {
        NUMBER,
        FORMULA
    }

    public Training() {
        createNumberLists();
    }

    protected void createNumberLists() {
        numbers_1 = IntStream.rangeClosed(rangeLow, rangeHigh).boxed().collect(Collectors.toList());
        numbers_2 = new ArrayList<>(numbers_1);
    }

    public String question() {
        if (progressive) {
            rangeHigh = Math.max(successCount, 9);
            createNumberLists();
        }
        count++;
        generate1();
        generate2();
        return String.format(EXAMPLE_FORMAT, a, b, "");
    }

    public String answer(String ans) {
        ans = ans.replace(" ", "").replace(":", "/");
        MatchParser parser = new MatchParser();
        var intAns = parser.parseInt(ans);

        var builder = new AnsiStringBuilder();
        if (checkAnswer(intAns)) {
            successCount++;
            builder
                    .color(AnsiStringBuilder.ForegroundColor.LIGHT_GREEN)
                    .append("Правильно !")
                    .reset()
                    .append("    " + rightAnswer());
            score(builder);
        } else {
            failCount++;
            builder
                    .color(AnsiStringBuilder.ForegroundColor.LIGHT_RED)
                    .append("Неправильно !!!")
                    .reset()
                    .line()
                    .append("Правильный ответ: " + rightAnswer());
            score(builder);
        }
        return builder.toString();
    }

    protected void score(AnsiStringBuilder builder) {
        builder
                .line()
                .append(" ".repeat(50))
                .color(AnsiStringBuilder.ForegroundColor.WHITE)
                .color(failCount > successCount ?
                        AnsiStringBuilder.BackgroundColor.RED :
                        AnsiStringBuilder.BackgroundColor.GREEN)
                .append(" " + successCount + " : " + failCount + " ")
                .reset()
                .line();
    }

    protected void generate1() {
        a = numbers_1.get((int) ((numbers_1.size()) * random.nextDouble()));
    }

    protected void generate2() {
        b = numbers_2.get((int) ((numbers_2.size()) * random.nextDouble()));
    }

    protected boolean checkAnswer(int ans) {
        return true;
    }

    public boolean checkInput(String input) {
        switch (inputType) {
            case NUMBER:
                return input.matches("-?(0|[1-9]\\d*)");
            case FORMULA:
                var symbols = Arrays.asList('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', ' ', '+', '-', '*', '/', ':');
                for (char c : input.toCharArray()) {
                    if (!symbols.contains(c)) {
                        return false;
                    }
                }
        }
        return true;
    }

    protected String rightAnswer() {
        return "";
    }

    protected void copyScore(Training training) {
        count = training.count;
        successCount = training.successCount;
        failCount = training.failCount;
    }

}
