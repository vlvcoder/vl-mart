package com.antonov.vlmart.model.place;

import com.antonov.vlmart.model.enums.GOOD;

public class SupplyPlace extends Place{

    public SupplyPlace(GOOD good, int capacity) {
        super(good, capacity, capacity);
    }

    @Override
    public String toString() {
        return String.format("%-10s\tКоличество: %4d", good.title(), stock);
    }

}
