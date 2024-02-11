package com.antonov.vlmart.service.training;

public class EquationSubFirst extends EquationAddFirst{

    public EquationSubFirst() {
        EXAMPLE_FORMAT = "%s - %s = %s\nx = ";
    }

    @Override
    public String question() {
        super.question();
        return String.format(EXAMPLE_FORMAT, "x", a, b);
    }

    @Override
    protected boolean checkAnswer(int ans) {
        return ans == a + b;
    }

    @Override
    protected String rightAnswer() {
        return String.format("x = %d + %d = %d", b, a, (a + b));
    }
}
