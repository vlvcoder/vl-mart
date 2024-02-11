package com.antonov.vlmart.model.place;

import com.antonov.vlmart.model.enums.FILL_LEVEL;
import com.antonov.vlmart.model.enums.GOOD;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(fluent = true)
@AllArgsConstructor
public class Place {

    public GOOD good;
    public int capacity;
    public volatile int stock;

    @Override
    public String toString() {
        return String.format("%-10s\t%4d\t%4d", good.title(), stock, capacity);
    }

    public FILL_LEVEL getFillLevel() {
        int fillPercent = (int) ((float) stock / (float) capacity * 100);
        return fillPercent < FILL_LEVEL.LOW.getLevel()
                ? FILL_LEVEL.LOW
                : fillPercent < FILL_LEVEL.NORMAL.getLevel()
                ? FILL_LEVEL.NORMAL
                : FILL_LEVEL.HIGH;
    }
}
