package com.antonov.vlmart.utils;

import com.antonov.vlmart.model.business.Estimation;
import com.antonov.vlmart.model.buyer.Buyer;
import com.antonov.vlmart.model.quant.Quant;
import com.antonov.vlmart.model.staff.Staff;
import com.antonov.vlmart.model.supply.SupplyBlock;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

@UtilityClass
public class Utils {
    public static String formatDateTime(long millis) {
        var res = Instant
                .ofEpochMilli(millis)
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return formatter.format(res);
    }

    public static void statusBuyerList(@NotNull List<Buyer> buyers, AnsiStringBuilder builder) {
        buyers.stream()
                .sorted((b1, b2) -> {
                    int ord = b1.status().order() - b2.status().order();
                    if (ord == 0) {
                        return Long.compare(b2.startMillis(), b1.startMillis());
                    }
                    return ord;
                })
                .forEach(buyer -> builder
                        .append(buyer.toString())
                        .reset()
                        .line());
    }

    public static void statusSupplyBlockList(List<SupplyBlock> blocks, AnsiStringBuilder builder) {
        statusSupplyBlockList(blocks, builder, false);
    }

    public static void statusSupplyBlockList(@NotNull List<SupplyBlock> blocks, AnsiStringBuilder builder, boolean brief) {
        blocks.stream()
                .sorted(Comparator.comparingLong(SupplyBlock::startMillis).reversed())
                .forEach(block -> {
                    if (brief) {
                        builder.reset()
                                .append("| ")
                                .line()
                                .append("| ");
                    }
                    switch (block.status()) {
                        case WAIT:
                            builder.color(AnsiStringBuilder.ForegroundColor.LIGHT_MAGENTA);
                            break;
                        case DELIVERED:
                            builder.color(AnsiStringBuilder.ForegroundColor.LIGHT_GREEN);
                            break;
                        case UNLOADING:
                            builder.color(AnsiStringBuilder.ForegroundColor.LIGHT_CYAN);
                            break;
                    }
                    builder
                            .append(brief ? block.briefInfo() : block.toString())
                            .reset()
                            .line();
                });
    }

    public static void statusQuantList(List<Quant> quants, AnsiStringBuilder builder) {
        quants.stream()
                .sorted(Comparator.comparingLong(Quant::startMillis).reversed())
                .forEach(quant -> {
                    builder
                            .color(AnsiStringBuilder.ForegroundColor.WHITE)
                            .append(quant.toString())
                            .reset()
                            .line();
                });
    }

    public static String error(String message) {
        return new AnsiStringBuilder().error(message).toString();
    }

    public static String warn(String message) {
        return new AnsiStringBuilder()
                .bold()
                .color(AnsiStringBuilder.ForegroundColor.YELLOW)
                .append(message).toString();
    }


    public static String longNumberFormat(long number) {
        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.getDefault());
        DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();

        symbols.setGroupingSeparator(' ');
        formatter.setDecimalFormatSymbols(symbols);
        return formatter.format(number);
    }

    public static void statusStaffList(List<Staff> staffs, AnsiStringBuilder builder) {
        staffs.forEach(quant -> {
            builder
                    .append(quant.toString())
                    .reset()
                    .line();
        });
    }
}
