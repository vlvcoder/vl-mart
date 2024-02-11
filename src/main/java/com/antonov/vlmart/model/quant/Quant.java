package com.antonov.vlmart.model.quant;

import com.antonov.vlmart.model.enums.QUANT_TYPE;
import com.antonov.vlmart.model.staff.Staff;
import com.antonov.vlmart.utils.Utils;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
public class Quant {
    private final QUANT_TYPE quantType;
    private final Staff staff;
    private final long startMillis = System.currentTimeMillis();

    @Setter
    private int durationSec;

    public Quant(QUANT_TYPE quantType, Staff staff) {
        this.quantType = quantType;
        this.staff = staff;
    }

    protected String info() {
        return "";
    }

    @Override
    public String toString() {
        return String.format(
                "%-10s\t%-18s\t%-12s\t%-8s",
                Utils.formatDateTime(startMillis),
                quantType.getTitle(),
                info(),
                staff.toString());
    }
}
