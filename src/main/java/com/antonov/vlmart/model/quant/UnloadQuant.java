package com.antonov.vlmart.model.quant;

import com.antonov.vlmart.model.enums.QUANT_TYPE;
import com.antonov.vlmart.model.staff.Staff;
import lombok.Getter;

@Getter
public class UnloadQuant extends Quant {

    private final int goodsCount;

    public UnloadQuant(Staff staff, int goodsCount) {
        super(QUANT_TYPE.UNLOAD, staff);
        this.goodsCount = goodsCount;
    }

    @Override
    protected String info() {
        return String.format("%14d", goodsCount);
    }
}
