package com.antonov.vlmart.render;

import com.antonov.vlmart.model.enums.SUPPLY_CALL;
import com.antonov.vlmart.utils.Utils;
import lombok.Data;

@Data
public class SupplyCallWrap {
    private boolean enabled;
    private boolean active;
    private final long priceValue;
    private final String price;
    private final String title;
    private final String name;

    public SupplyCallWrap(SUPPLY_CALL supplyCall) {
        this.name = supplyCall.name();
        this.enabled = supplyCall.isEnabled();
        this.active = supplyCall.isActive();
        this.priceValue = supplyCall.getPrice();
        this.price = Utils.longNumberFormat(supplyCall.getPrice());
        this.title = supplyCall.getTitle();
    }
}
