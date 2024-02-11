package com.antonov.vlmart.service.training;

public class EquationMultiFirst extends EquationAddFirst{

    public EquationMultiFirst() {
        EXAMPLE_FORMAT = "%s * %s = %s\nx = ";
    }

    @Override
    public String question() {
        super.question();
        return String.format(EXAMPLE_FORMAT, "x", b, a * b);
    }

    @Override
    protected String rightAnswer() {
        return String.format("x = %d : %d = %d", (a * b), b, a);
    }
}
