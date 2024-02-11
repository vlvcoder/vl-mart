package com.antonov.vlmart.model.area;

import com.antonov.vlmart.model.place.Place;

import java.util.List;

public interface PlaceOwner {
    List<Place> getPlaces();
    String color();
}
