package com.antonov.vlmart.service.training;

public class EquationSubSecond extends EquationAddSecond{

    public EquationSubSecond() {
        EXAMPLE_FORMAT = "%s - %s = %s\nx = ";
    }

    @Override
    public String question() {
        super.question();
        return String.format(EXAMPLE_FORMAT, a + b, "x", b);
    }

    @Override
    protected boolean checkAnswer(int ans) {
        return ans == a;
    }

    @Override
    protected String rightAnswer() {
        return String.format("x = %d - %d = %d", (a + b), b, a);
    }
}
