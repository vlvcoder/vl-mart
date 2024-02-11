package com.antonov.vlmart.model;

import com.antonov.vlmart.model.area.*;
import com.antonov.vlmart.model.enums.SPACE_TYPE;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(fluent = true)
public class Mart {
    private boolean isOpened;
    private boolean staffFolderExpanded = true;

    private Store store = new Store();
    private Shop shop = new Shop();
    private Employee employee = new Employee();
    private Supply supply = new Supply();
    private Board board = new Board();
    private Hall hall = new Hall();

    public PlaceOwner placeOwner(SPACE_TYPE spaceType) {
        switch (spaceType) {
            case SHOP:
                return shop;
            case STORE:
                return store;
        }
        return null;
    }
}
