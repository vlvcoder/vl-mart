package com.antonov.vlmart.cli;

import com.antonov.vlmart.model.area.PlaceOwner;
import com.antonov.vlmart.utils.AnsiStringBuilder;

public class PlaceOwnerFormatter implements CliFormatter {

    public AnsiStringBuilder build(PlaceOwner placeOwner) {
        return build(placeOwner, null);
    }

    public AnsiStringBuilder build(PlaceOwner placeOwner, AnsiStringBuilder builder) {
        if (builder == null) {
            builder = new AnsiStringBuilder();
        }

        //todo Списки товаров сортировать
        buildHeader(builder, placeOwner.toString(), placeOwner.color());
        for (var place : placeOwner.getPlaces()) {
            switch (place.getFillLevel()) {
                case LOW:
                    builder.color(AnsiStringBuilder.ForegroundColor.RED);
                    break;
                case HIGH:
                    builder.color(AnsiStringBuilder.ForegroundColor.GREEN);
                    break;
            }
            builder
                    .append(new PlaceFormatter().build(place).toString())
                    .reset()
                    .line();
        }
        return builder;
    }

}
