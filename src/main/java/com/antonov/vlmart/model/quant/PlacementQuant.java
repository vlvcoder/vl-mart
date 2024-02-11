package com.antonov.vlmart.model.quant;

import com.antonov.vlmart.model.enums.GOOD;
import com.antonov.vlmart.model.enums.QUANT_TYPE;
import com.antonov.vlmart.model.staff.Staff;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
public class PlacementQuant extends Quant {
    private final GOOD good;
    private final int quantity;
    @Setter
    private int quantity_completed;

    public PlacementQuant(QUANT_TYPE quantType, Staff staff, GOOD good, int quantity) {
        super(quantType, staff);
        this.good = good;
        this.quantity = quantity;
    }

    @Override
    protected String info() {
        return String.format("%-8s %4s", good.title(), "[" + quantity_completed + "/" + quantity + "]");
    }
}
