package com.antonov.vlmart.model.buyer;

import com.antonov.vlmart.model.enums.BUYER_STATUS;
import com.antonov.vlmart.utils.AnsiStringBuilder;
import com.antonov.vlmart.utils.Utils;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
@Accessors(fluent = true)
public class Buyer {

    private final String id = UUID.randomUUID().toString();
    private final List<Purchase> purchases = new ArrayList<>();
    private volatile BUYER_STATUS status = BUYER_STATUS.BUY;

    private final long startMillis;
    private volatile long finishMillis;
    private volatile long nextStepMillis;

    public Buyer() {
        startMillis = System.currentTimeMillis();
    }

    @Override
    public String toString() {
        var builder = new AnsiStringBuilder();
        switch (status) {
            case BUY:
                builder.color(AnsiStringBuilder.ForegroundColor.WHITE);
                break;
            case CASH_WAIT:
                builder.color(AnsiStringBuilder.ForegroundColor.YELLOW);
                break;
            case CASH:
                builder.color(AnsiStringBuilder.ForegroundColor.GREEN);
                break;
        }
        builder
                .append(String.format("%s\t", Utils.formatDateTime(startMillis)))
                .reset()
                .append(purchsesToString());
        return builder.toString();
    }

    private String purchsesToString() {
        return purchases.stream()
                .map(Purchase::toString)
                .sorted()
                .collect(Collectors.joining(" - "));
    }

}
