package com.antonov.vlmart.service.training;

public class EquationAddSecond extends EquationAddFirst {

    @Override
    public String question() {
        super.question();
        return String.format(EXAMPLE_FORMAT, a, "x", a + b);
    }

    @Override
    protected boolean checkAnswer(int ans) {
        return ans == b;
    }

    @Override
    protected String rightAnswer() {
        return String.format("x = %d - %d = %d", (a + b), a, b);
    }

}
