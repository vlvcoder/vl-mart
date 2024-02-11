package com.antonov.vlmart.service.config;

import com.antonov.vlmart.model.enums.PLACE_VOLUME;
import com.antonov.vlmart.render.PlaceExpandWrap;
import com.antonov.vlmart.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public interface CapacityConfiguration {
    int capacityByVolume(PLACE_VOLUME volume);
    int expandCostByVolume(PLACE_VOLUME volume);
    default List<PlaceExpandWrap> expandVariants() {
        var variants = new ArrayList<PlaceExpandWrap>();
        for (PLACE_VOLUME volume : PLACE_VOLUME.values()) {
            int price = expandCostByVolume(volume);
            variants.add(new PlaceExpandWrap(
                    volume,
                    capacityByVolume(volume),
                    price,
                    Utils.longNumberFormat(price)));
        }
        return variants;
    }



}
