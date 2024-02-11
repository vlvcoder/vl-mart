package com.antonov.vlmart.render;

import com.antonov.vlmart.model.enums.PLACE_VOLUME;
import lombok.Data;

@Data
public class PlaceExpandWrap {
    private final PLACE_VOLUME volume;
    private final int quantity;
    private final int price;
    private final String priceString;

    public PlaceExpandWrap(
            PLACE_VOLUME volume,
            int quantity,
            int price,
            String priceString) {
        this.volume = volume;
        this.quantity = quantity;
        this.price = price;
        this.priceString = priceString;
    }
}
