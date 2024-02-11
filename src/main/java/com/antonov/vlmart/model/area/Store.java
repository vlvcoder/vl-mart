package com.antonov.vlmart.model.area;

import com.antonov.vlmart.model.place.Place;
import com.antonov.vlmart.utils.AnsiStringBuilder;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

@Data
@Accessors(fluent = true)
public class Store implements Area, PlaceOwner {
    private List<Place> places = new ArrayList<>();

    @Override
    public String toString() {
        return "Склад";
    }

    @Override
    public String statusText() {
        var builder = statusHeader("#fc6600")
                .reset();
        for (var place : places) {
            switch (place.getFillLevel()) {
                case LOW:
                    builder.color(AnsiStringBuilder.ForegroundColor.RED);
                    break;
                case HIGH:
                    builder.color(AnsiStringBuilder.ForegroundColor.GREEN);
                    break;
            }
            builder
                    .append(place.toString())
                    .reset()
                    .line();
        }
        return builder.toString();
    }

    @Override
    public List<Place> getPlaces() {
        return places;
    }

    @Override
    public String color() {
        return "#fc6600";
    }
}
