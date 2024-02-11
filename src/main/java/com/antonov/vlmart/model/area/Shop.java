package com.antonov.vlmart.model.area;

import com.antonov.vlmart.model.place.Place;
import com.antonov.vlmart.utils.AnsiStringBuilder;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

@Data
@Accessors(fluent = true)
public class Shop implements Area, PlaceOwner {
    private List<Place> places = new ArrayList<>();

    @Override
    public String statusText() {
        //todo Списки товаров сортировать
        var builder = statusHeader("#e1cc4f")
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
        //todo Ввести formatters для каждого объекта
//        builder.maxStringLength()

        return builder.line().toString();
    }

    @Override
    public String toString() {
        return "Полки";
    }

    @Override
    public List<Place> getPlaces() {
        return places;
    }

    @Override
    public String color() {
        return "#e1cc4f";
    }
}
