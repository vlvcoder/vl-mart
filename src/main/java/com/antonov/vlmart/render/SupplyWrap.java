package com.antonov.vlmart.render;

import com.antonov.vlmart.model.enums.SUPPLY_BLOCK_STATUS;
import com.antonov.vlmart.model.place.Place;
import com.antonov.vlmart.model.supply.SupplyBlock;
import lombok.Data;

@Data
public class SupplyWrap {
    public SupplyWrap(SupplyBlock supplyBlock, long durationSec) {
        this.status = supplyBlock.status();
        this.startMillis = supplyBlock.startMillis();
        this.startUnloadMillis = supplyBlock.startUnloadMillis();
        this.completeMillis = supplyBlock.completeMillis();
        this.durationSec = durationSec;
        this.quantity = supplyBlock.places().stream().mapToInt(Place::stock).sum();
    }

    private SUPPLY_BLOCK_STATUS status;
    private final long startMillis;
    private final long startUnloadMillis;
    private final long completeMillis;
    private final long durationSec;
    private final long quantity;

}
