package com.antonov.vlmart.model.area;

import com.antonov.vlmart.utils.AnsiStringBuilder;

public interface Area {
    String statusText();

    default AnsiStringBuilder statusHeader(String hexColor) {
        try {
            return new AnsiStringBuilder()
                    .reset()
                    .bold()
                    .italic()
                    .underline()
                    .color24(hexColor)
                    .append(this.toString())
                    .reset()
                    .line();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
