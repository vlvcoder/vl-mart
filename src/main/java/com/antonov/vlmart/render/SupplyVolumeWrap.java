package com.antonov.vlmart.render;

import com.antonov.vlmart.model.enums.SUPPLY_VOLUME;
import com.antonov.vlmart.utils.Utils;
import lombok.Data;

@Data
public class SupplyVolumeWrap {
    private boolean enabled;
    private boolean active;
    private final long priceValue;
    private final String price;
    private final String title;
    private final String name;

    public SupplyVolumeWrap(SUPPLY_VOLUME supplyVolume) {
        this.name = supplyVolume.name();
        this.enabled = supplyVolume.isEnabled();
        this.active = supplyVolume.isActive();
        this.priceValue = supplyVolume.getPrice();
        this.price = Utils.longNumberFormat(supplyVolume.getPrice());
        this.title = supplyVolume.getTitle();
    }
}
