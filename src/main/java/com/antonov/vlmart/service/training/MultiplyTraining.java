package com.antonov.vlmart.service.training;

import com.antonov.vlmart.utils.AnsiStringBuilder;

public class MultiplyTraining extends Training {

    public MultiplyTraining() {
        inputType = INPUT_TYPE.NUMBER;
    }

    @Override
    public String answer(String ans) {
        var builder = new AnsiStringBuilder();
        if (!checkAnswer(Integer.parseInt(ans))) {
            pifagorTable(builder, a, b);
        }
        return super.answer(ans) + builder;
    }

    @Override
    protected boolean checkAnswer(int ans) {
        return ans == a * b;
    }

    @Override
    protected String rightAnswer() {
        return String.format(EXAMPLE_FORMAT, a, b, a * b);
    }

    public AnsiStringBuilder pifagorTable(AnsiStringBuilder builder) {
        return pifagorTable(builder,0,0);
    }

    public AnsiStringBuilder pifagorTable(AnsiStringBuilder builder, int m, int n) {
        final String delim = "     ";
        builder.reset().line().append("       ");
        builder.color(AnsiStringBuilder.ForegroundColor.LIGHT_BLUE);
        for (int i = 1; i < 10; i++) {
            builder.append(String.format("%2d%s", i, delim));
        }
        builder
                .line()
                .color(AnsiStringBuilder.ForegroundColor.WHITE)
                .append("    " + "_".repeat(61));

        for (int i = 1; i < 10; i++) {
            builder
                    .line()
                    .appendLine("   " + "|")
                    .color(AnsiStringBuilder.ForegroundColor.LIGHT_BLUE)
                    .append(i)
                    .color(AnsiStringBuilder.ForegroundColor.WHITE)
                    .append("  " + "|   ");
            for (int j = 1; j < 10; j++) {
                builder
                        .color(getNumberColor(i, j, m, n))
                        .color(getBackColor(i, j, m, n))
                        .append(String.format("%2d", i * j))
                        .reset()
                        .append(delim);
            }
        }
        return builder.line();
    }

    private AnsiStringBuilder.BackgroundColor getBackColor(int i, int j, int m, int n) {
        return i == m && j == n ?
                AnsiStringBuilder.BackgroundColor.BLUE :
                AnsiStringBuilder.BackgroundColor.RESET;
    }

    private AnsiStringBuilder.ForegroundColor getNumberColor(int i, int j, int m, int n) {
        if (i == m && j == n) {
            return AnsiStringBuilder.ForegroundColor.WHITE;
        }
        else if (i == m || j == n) {
            return AnsiStringBuilder.ForegroundColor.YELLOW;
        }
        else if (i == j) {
            return AnsiStringBuilder.ForegroundColor.LIGHT_GREEN;
        }
        return AnsiStringBuilder.ForegroundColor.RESET;
    }

}
