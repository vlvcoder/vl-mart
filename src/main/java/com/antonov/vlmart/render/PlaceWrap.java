package com.antonov.vlmart.render;

import com.antonov.vlmart.model.enums.GOOD;
import com.antonov.vlmart.model.place.Place;
import lombok.Getter;
import lombok.Setter;

public class PlaceWrap extends Place {

    @Getter
    @Setter
    private String title;

    @Getter
    @Setter
    private int waitingSupply;

    public PlaceWrap(Place place) {
        super(place.good, place.capacity, place.stock);
        title = place.good.title();
    }

    public PlaceWrap(GOOD good, int capacity, int stock) {
        super(good, capacity, stock);
    }
}
