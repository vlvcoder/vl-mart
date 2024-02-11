package com.antonov.vlmart.utils;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ColumnsBuilder {
    private final String DELIMETER = "\t\t\t";

    private final List<StringBuilder> builders = new ArrayList<>();

    public ColumnsBuilder(int columnCount) {
        for (int i = 0; i < columnCount; i++) {
            builders.add(new StringBuilder());
        }
    }

    public ColumnsBuilder append(int column ,String str) {
        if (column >= builders.size()) {
            throw new RuntimeException("Неверный номер столбца. Ожидается 0.." + (builders.size() - 1));
        }
        builders.get(column).append(str).append(System.lineSeparator());
        return this;
    }

    @Override
    public String toString() {
        StringBuilder res = new StringBuilder();
        var columns = slpitLines();
        var maxStringInColumn = builders.stream()
                .map(StringBuilder::toString)
                .map(this::removeAnsiSymbols)
                .map(this::maxLineLength)
                .collect(Collectors.toList());
        var rowCount = columns.stream()
                .mapToInt(Array::getLength)
                .max()
                .orElse(0);

        for (int row = 0; row < rowCount; row++) {
            for (int col = 0; col < columns.size(); col++) {
                var column = columns.get(col);
                String s = "";
                if (column.length > row) {
                    s = column[row];
                }
                s += " ".repeat(maxStringInColumn.get(col) -  removeAnsiSymbols(s).length());
                res.append(s);
                res.append(DELIMETER);
            }
            res.append(System.lineSeparator());
        }
        return res.toString();
    }

    private List<String[]> slpitLines() {
        List<String[]> list = new ArrayList<>();
        for (StringBuilder builder : builders) {
            String[] column = builder.toString().split(System.lineSeparator());
            list.add(column);
        }
        return list;
    }

    private String removeAnsiSymbols(String s) {
        var builder = new StringBuilder();
        while(true) {
            int start = s.indexOf("\u001B[");
            if (s.length() == 0 || start < 0) {
                builder.append(s);
                break;
            }
            builder.append(s, 0, start);
            s = s.substring(start);
            int m  = s.indexOf("m") + 1;
            s = s.substring(Math.min(m, s.length()));
        }
        return builder.toString().replace("\t", "    ");
    }

    private int maxLineLength(String s) {
        return Arrays.stream(s.split(System.lineSeparator()))
                .mapToInt(String::length)
                .max()
                .orElse(0);
    }

}
