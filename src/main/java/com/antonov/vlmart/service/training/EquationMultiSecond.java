package com.antonov.vlmart.service.training;

public class EquationMultiSecond extends EquationAddSecond {

    public EquationMultiSecond() {
        EXAMPLE_FORMAT = "%s * %s = %s\nx = ";
    }

    @Override
    public String question() {
        super.question();
        return String.format(EXAMPLE_FORMAT, a, "x", a * b);
    }

    @Override
    protected String rightAnswer() {
        return String.format("x = %d : %d = %d", (a * b), a, b);
    }
}
