package com.antonov.vlmart.model.supply;

import com.antonov.vlmart.model.enums.SUPPLY_BLOCK_STATUS;
import com.antonov.vlmart.model.place.Place;
import com.antonov.vlmart.model.place.SupplyPlace;
import com.antonov.vlmart.utils.Utils;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

@Data
@Accessors(fluent = true)
public class SupplyBlock {
    private final List<SupplyPlace> places = new ArrayList<>();

    private SUPPLY_BLOCK_STATUS status = SUPPLY_BLOCK_STATUS.WAIT;

    private final long startMillis = System.currentTimeMillis();
    private long startUnloadMillis;
    private long completeMillis;

    @Override
    public String toString() {
        return String.format(
                "%-10s\t%-8s\t%4d\t%4d",
                Utils.formatDateTime(startMillis),
                status.getTitle(),
                places.size(),
                places.stream().mapToInt(Place::stock).sum());
    }

    public String briefInfo() {
        return String.format(
                "%-10s:\t%5d",
                status.getTitle(),
                places.stream().mapToInt(Place::stock).sum());
    }

}
