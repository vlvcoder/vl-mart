package com.antonov.vlmart.model.area;

import com.antonov.vlmart.utils.AnsiStringBuilder;
import com.antonov.vlmart.utils.Utils;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(fluent = true)
public class Business implements Area{
    private long money;
    private int like;
    private int dislike;
    private boolean reachMillion;

    @Override
    public String toString() {
        return "Бизнес";
    }

    private final long startMillis = System.currentTimeMillis();

    @Override
    public String statusText() {
        var builder = new AnsiStringBuilder()
                .line()
                .line()
                .append("| ")
                .color(AnsiStringBuilder.ForegroundColor.LIGHT_YELLOW)
                .append(String.format("%-10s\t%5s", "Деньги:", Utils.longNumberFormat(money)))
                .reset()
                .line()
                .append("| ")
                .line()
                .append("| ")
                .color(AnsiStringBuilder.ForegroundColor.LIGHT_GREEN)
                .append(String.format("%-10s\t%5d", "Лайки: ", like))
                .reset()
                .line()
                .append("| ")
                .line()
                .append("| ")
                .color(AnsiStringBuilder.ForegroundColor.LIGHT_RED)
                .append(String.format("%-10s\t%5d", "Дизлайки: ", dislike))
                .reset()
                .line();
        return builder.toString();
    }
}
