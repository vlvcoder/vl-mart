package com.antonov.vlmart.model.business;

import com.antonov.vlmart.model.enums.ESTIMATION_TYPE;
import com.antonov.vlmart.utils.AnsiStringBuilder;
import com.antonov.vlmart.utils.Utils;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(fluent = true)
public class Estimation {
    private final ESTIMATION_TYPE estimationType;
    private final long createMillis = System.currentTimeMillis();

    public Estimation(ESTIMATION_TYPE estimationType) {
        this.estimationType = estimationType;
    }

    @Override
    public String toString() {
        var builder = new AnsiStringBuilder();
        var color = estimationType.isPositive() ?
                AnsiStringBuilder.ForegroundColor.GREEN :
                AnsiStringBuilder.ForegroundColor.RED;
        builder
                .color(color)
                .append(String.format("%s\t", Utils.formatDateTime(createMillis)))
                .reset()
                .append(String.format("%-30s\t", estimationType.title()))
                .append(String.format("%3s\t", (estimationType.isPositive() ? "" : "-") + estimationType.value()));
        return builder.toString();
    }
}
