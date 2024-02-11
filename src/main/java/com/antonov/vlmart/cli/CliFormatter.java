package com.antonov.vlmart.cli;

import com.antonov.vlmart.utils.AnsiStringBuilder;

public interface CliFormatter {

    default AnsiStringBuilder buildHeader(AnsiStringBuilder builder, String title, String hexColor) {
        try {
            return builder
                    .reset()
                    .bold()
                    .italic()
                    .underline()
                    .color24(hexColor)
                    .append(title)
                    .reset()
                    .line();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
