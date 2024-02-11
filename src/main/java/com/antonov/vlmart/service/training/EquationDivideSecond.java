package com.antonov.vlmart.service.training;

public class EquationDivideSecond extends EquationAddSecond {

    public EquationDivideSecond() {
        EXAMPLE_FORMAT = "%s : %s = %s\nx = ";
    }

    @Override
    public String question() {
        super.question();
        return String.format(EXAMPLE_FORMAT, a * b, "x", a);
    }

    @Override
    protected String rightAnswer() {
        return String.format("x = %d : %d = %d", (a * b), a, b);
    }
}
