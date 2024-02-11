package com.antonov.vlmart.config;

import com.antonov.vlmart.model.enums.PLACE_VOLUME;
import com.antonov.vlmart.render.PlaceExpandWrap;
import com.antonov.vlmart.service.config.CapacityConfiguration;
import com.antonov.vlmart.utils.Utils;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Data
@Component
@ConfigurationProperties(prefix = "vlmart.store.place.capacity")
public class StoreCapacityConfiguration implements CapacityConfiguration {
    private int small;
    private int medium;
    private int large;
    private int expandcost;

    @Override
    public int capacityByVolume(PLACE_VOLUME volume) {
        switch (volume) {
            case SMALL:
                return small;
            case MEDIUM:
                return medium;
            default:
                return large;
        }
    }

    @Override
    public int expandCostByVolume(PLACE_VOLUME volume) {
        return (int) (expandcost * volume.getExpandCostCoeff());
    }

}
