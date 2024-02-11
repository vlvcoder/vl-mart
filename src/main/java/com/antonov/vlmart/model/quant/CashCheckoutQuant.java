package com.antonov.vlmart.model.quant;

import com.antonov.vlmart.model.enums.QUANT_TYPE;
import com.antonov.vlmart.model.staff.Staff;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
public class CashCheckoutQuant extends Quant{
    private final String buyerId;
    private final int unitCount;

    public CashCheckoutQuant(
            QUANT_TYPE quantType,
            Staff staff,
            String buyerId,
            int unitCount) {
        super(quantType, staff);
        this.buyerId = buyerId;
        this.unitCount = unitCount;
    }

    @Override
    protected String info() {
        return String.format("%14d", unitCount);
    }
}
