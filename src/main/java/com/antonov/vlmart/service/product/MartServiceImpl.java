package com.antonov.vlmart.service.product;

import com.antonov.vlmart.config.MainConfiguration;
import com.antonov.vlmart.config.ShopCapacityConfiguration;
import com.antonov.vlmart.config.StoreCapacityConfiguration;
import com.antonov.vlmart.model.Mart;
import com.antonov.vlmart.model.area.*;
import com.antonov.vlmart.model.enums.*;
import com.antonov.vlmart.model.place.Place;
import com.antonov.vlmart.render.*;
import com.antonov.vlmart.service.notify.NotificationService;
import com.antonov.vlmart.utils.AnsiStringBuilder;
import com.antonov.vlmart.utils.ColumnsBuilder;
import com.antonov.vlmart.wrap.Liker;
import com.antonov.vlmart.wrap.SupplyCaller;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MartServiceImpl implements MartService {
    private final StoreCapacityConfiguration storeCapacityConfiguration;
    private final ShopCapacityConfiguration shopCapacityConfiguration;

    private final NotificationService notificationService;
    private final MainConfiguration mainConfiguration;
    private Liker liker;
    private SupplyCaller supplyCaller;

    private int goodGroupNextIndex = 1;

    public MartServiceImpl(
            StoreCapacityConfiguration storeCapacityConfiguration,
            ShopCapacityConfiguration shopCapacityConfiguration,
            NotificationService notificationService,
            MainConfiguration mainConfiguration) {
        this.storeCapacityConfiguration = storeCapacityConfiguration;
        this.shopCapacityConfiguration = shopCapacityConfiguration;
        this.notificationService = notificationService;
        this.mainConfiguration = mainConfiguration;
    }

    private Mart mart;

    @Override
    public void init() {
        mart = new Mart();

        boolean store_full = mainConfiguration.isStore_full();
        boolean shop_full = mainConfiguration.isShop_full();

        goodGroupNextIndex = 1;
        Arrays.stream(GOOD.values())
                .filter(good -> good.groupIndex() == 0)
                .forEach(this::createPlace);

        if (!store_full) {
            mart.store().getPlaces().forEach(place -> place.stock(0));
        }
        if (!shop_full) {
            mart.shop().getPlaces().forEach(place -> place.stock(0));
        }
        mart.staffFolderExpanded(true);
    }

    @Override
    public void open() {
        mart.isOpened(true);
    }

    @Override
    public void close() {
        if (mart.isOpened()) {
            liker.like(ESTIMATION_TYPE.CLOSED);
        }
        mart.isOpened(false);
    }

    @Override
    public boolean opened() {
        return mart.isOpened();
    }

    @Override
    public String status() {
        return mart.supply().statusText() +
                mart.board().statusText() +
                mart.hall().statusText() +
                new ColumnsBuilder(3)
                        .append(0, shopStatus())
                        .append(1, storeStatus())
                        .append(2, totalStatus())
                        .toString();
    }

    @Override
    public String shopStatus() {
        return mart.shop().statusText();
    }

    @Override
    public String storeStatus() {
        return mart.store().statusText();
    }

    @Override
    public String totalStatus() {
        return statusOpened() + liker.statusText() + mart.supply().briefStatus();
    }

    private String statusOpened() {
        var res = new AnsiStringBuilder()
                .color(AnsiStringBuilder.ForegroundColor.WHITE);
        if (opened()) {
            res
                    .color(AnsiStringBuilder.BackgroundColor.GREEN)
                    .append(" Открыто ");
        } else {
            res
                    .color(AnsiStringBuilder.BackgroundColor.RED)
                    .append(" Закрыто ");
        }
        return res
                .reset()
                .toString();
    }

    @Override
    public Place getStorePlace(GOOD good) {
        return mart.store()
                .getPlaces().stream()
                .filter(p -> p.good().equals(good))
                .findFirst()
                .orElse(null);
    }

    @Override
    public Place getShopPlace(GOOD good) {
        return mart.shop()
                .getPlaces().stream()
                .filter(p -> p.good().equals(good))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Place> storePlaces() {
        return mart.store().getPlaces();
    }

    @Override
    public List<Place> shopPlaces() {
        return mart.shop().getPlaces();
    }

    @Override
    public Supply getSupply() {
        return mart.supply();
    }

    @Override
    public Board getBoard() {
        return mart.board();
    }

    @Override
    public Hall getHall() {
        return mart.hall();
    }

    @Override
    public synchronized int take(Place place, int quantity) {
        quantity = Math.max(0, quantity);
        quantity = Math.min(place.stock(), quantity);
        place.stock(place.stock() - quantity);
        return quantity;
    }

    @Override
    public int takeFromShop(GOOD good, int quantity) {
        var place = getShopPlace(good);
        int res = take(place, quantity);
        checkLowStock();
        return res;
    }

    private void checkLowStock() {
        long lowStockPlaceCount = mart.shop().places().stream()
                .filter(place -> place.getFillLevel() == FILL_LEVEL.LOW)
                .count();
        if (lowStockPlaceCount == mart.shop().places().size()) {
            notificationService.sendOnce(NOTIFY_TYPE.ALL_LOW_STOCK_IN_SHOP);
        }
        else if (lowStockPlaceCount > 0) {
            notificationService.sendOnce(NOTIFY_TYPE.LOW_STOCK_IN_SHOP);
        }
    }

    @Override
    public int takeFromStore(GOOD good, int quantity) {
        var place = getStorePlace(good);
        int res = take(place, quantity);
        checkSupplyNeed();
        return res;
    }

    private void checkSupplyNeed() {
        long lowStockPlaceCount = mart.store().places().stream()
                .filter(place -> place.getFillLevel() == FILL_LEVEL.LOW)
                .count();
        if (lowStockPlaceCount == mart.store().places().size()) {
            notificationService.sendOnce(NOTIFY_TYPE.ALL_SUPPLY_NEEDED);
        }
        else if (lowStockPlaceCount > 0) {
            notificationService.sendOnce(NOTIFY_TYPE.SUPPLY_NEEDED);
        }
        if (lowStockPlaceCount > 0) {
            supplyCaller.callSupply(SUPPLY_CALL.CALL_LOW_LEWEL);
        }
    }

    @Override
    public synchronized int put(Place place, int quantity) {
        quantity = Math.max(0, quantity);
        int remains = place.capacity() - place.stock();
        quantity = Math.min(remains, quantity);
        place.stock(place.stock() + quantity);
        return quantity;
    }

    @Override
    public int putToShop(GOOD good, int quantity) {
        var place = getShopPlace(good);
        return put(place, quantity);
    }

    @Override
    public int putToStore(GOOD good, int quantity) {
        var place = getStorePlace(good);
        return put(place, quantity);
    }

    @Override
    public List<GOOD> getPresentedGoods() {
        return mart.shop()
                .getPlaces().stream()
                .map(Place::good)
                .collect(Collectors.toList());
    }

    @Override
    public void setLiker(Liker liker) {
        this.liker = liker;
    }

    @Override
    public void setSupplyCaller(SupplyCaller supplyCaller) {
        this.supplyCaller = supplyCaller;
    }

    @Override
    public boolean createPlace(GOOD good) {
        if (mart.shop().getPlaces().stream().anyMatch(p -> p.good() == good)) {
            return false;
        }

        int storePlaceCapacity = storeCapacityConfiguration.capacityByVolume(good.storePlaceVolume());
        int shopPlaceCapacity = shopCapacityConfiguration.capacityByVolume(good.shopPlaceVolume());

        mart.store().getPlaces().add(new Place(good, storePlaceCapacity, storePlaceCapacity));
        mart.shop().getPlaces().add(new Place(good, shopPlaceCapacity, shopPlaceCapacity));
        return true;
    }

    @Override
    public Place expandPlace(SPACE_TYPE spaceType, GOOD good, PLACE_VOLUME volume) {
        var space = spaceType == SPACE_TYPE.SHOP ?
                shopCapacityConfiguration :
                storeCapacityConfiguration;
        Place place = mart.placeOwner(spaceType)
                .getPlaces().stream()
                .filter(p -> p.good().equals(good))
                .findFirst()
                .orElse(null);
        if (place != null) {
            place.capacity(place.capacity() + space.capacityByVolume(volume));
        }
        return place;
    }

    @Override
    public PlaceOwner getShop() {
        return mart.shop();
    }

    @Override
    public PlaceOwner getStore() {
        return mart.store();
    }

    @Override
    public StatusWrap VLMartStatus() {
        List<Place> places = shopPlaces();
        return StatusWrap.builder()
                .currentMillis(System.currentTimeMillis())
                .opened(mart.isOpened())
                .staffFolderExpanded(isStaffFolderExpanded())
                .shopPlaces(places.stream().map(PlaceWrap::new).collect(Collectors.toList()))
                .storePlaces(storePlaces().stream().map(PlaceWrap::new).collect(Collectors.toList()))
                .mediatorWraps(places.stream().map(MediatorWrap::new).collect(Collectors.toList()))
                .goodsForAdd(goodsForAdd())
                .build();
    }

    @Override
    public void addAllGoods() {
        Arrays.stream(GOOD.values())
                .filter(good -> mart.shop()
                        .places().stream()
                        .noneMatch(place -> place.good == good))
                .forEach(this::createPlace);
    }

    private void addGroupGoods(int groupIndex) {
        Arrays.stream(GOOD.values())
                .filter(good -> good.groupIndex() == groupIndex)
                .forEach(this::createPlace);
    }

    @Override
    public void moneyChanged(int like) {
        if (goodGroupNextIndex > 0 &&
                goodGroupNextIndex < 3 &&
                like >= goodGroupNextIndex * mainConfiguration.getGood_group_update_likes()
        ) {
            addGroupGoods(goodGroupNextIndex++);
        }
    }

    @Override
    public boolean isStaffFolderExpanded() {
        return mart.staffFolderExpanded();
    }

    @Override
    public void setStaffFolderExpanded(boolean value) {
        mart.staffFolderExpanded(value);
    }

    private List<GoodWrap> goodsForAdd() {
        return Arrays.stream(GOOD.values())
                .filter(good -> good.groupIndex() < 0 &&
                        mart.shop().places().stream()
                        .noneMatch(place -> place.good == good))
                .map(GoodWrap::new)
                .collect(Collectors.toList());
    }
}
