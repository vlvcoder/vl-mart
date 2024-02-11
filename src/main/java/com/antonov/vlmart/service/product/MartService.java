package com.antonov.vlmart.service.product;

import com.antonov.vlmart.model.area.*;
import com.antonov.vlmart.model.enums.GOOD;
import com.antonov.vlmart.model.enums.PLACE_VOLUME;
import com.antonov.vlmart.model.enums.SPACE_TYPE;
import com.antonov.vlmart.model.place.Place;
import com.antonov.vlmart.render.StatusWrap;
import com.antonov.vlmart.wrap.Liker;
import com.antonov.vlmart.wrap.SupplyCaller;

import java.util.List;

public interface MartService {
    void init();

    void open();
    void close();
    boolean opened();

    String status();
    String shopStatus();
    String storeStatus();

    String totalStatus();

    Place getStorePlace(GOOD good);
    Place getShopPlace(GOOD good);
    List<Place> storePlaces();
    List<Place> shopPlaces();

    Supply getSupply();

    Board getBoard();
    Hall getHall();

    int take(Place place, int quantity);
    int takeFromShop(GOOD good, int quantity);
    int takeFromStore(GOOD good, int quantity);

    int put(Place place, int quantity);
    int putToShop(GOOD good, int quantity);
    int putToStore(GOOD good, int quantity);

    List<GOOD> getPresentedGoods();

    void setLiker(Liker liker);
    void setSupplyCaller(SupplyCaller supplyCaller);

    boolean createPlace(GOOD good);

    Place expandPlace(SPACE_TYPE spaceType, GOOD good, PLACE_VOLUME volume);

    PlaceOwner getShop();
    PlaceOwner getStore();

    StatusWrap VLMartStatus();

    void addAllGoods();

    void moneyChanged(int like);

    boolean isStaffFolderExpanded();
    void setStaffFolderExpanded(boolean value);
}
