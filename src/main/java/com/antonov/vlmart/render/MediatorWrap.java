package com.antonov.vlmart.render;

import com.antonov.vlmart.model.enums.GOOD;
import com.antonov.vlmart.model.place.Place;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class MediatorWrap {
    private GOOD good;
    private int buyersNeeds;
    private int movings;

    public MediatorWrap(Place place) {
        this.good = place.good;
    }

}
