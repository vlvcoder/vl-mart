package com.antonov.vlmart.cli;

import com.antonov.vlmart.model.place.Place;
import com.antonov.vlmart.utils.AnsiStringBuilder;

public class PlaceFormatter implements CliFormatter {

    public AnsiStringBuilder build(Place place) {
        return build(place, null);
    }

    public AnsiStringBuilder build(Place place, AnsiStringBuilder builder) {
        if (builder == null) {
            builder = new AnsiStringBuilder();
        }
        return builder.append(String.format("%-10s\t%4d\t%4d",
                place.good().title(),
                place.stock(),
                place.capacity()));
    }

}
