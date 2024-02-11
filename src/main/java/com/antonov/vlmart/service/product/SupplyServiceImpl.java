package com.antonov.vlmart.service.product;

import com.antonov.vlmart.config.SupplyConfiguration;
import com.antonov.vlmart.model.area.Supply;
import com.antonov.vlmart.model.enums.NOTIFY_TYPE;
import com.antonov.vlmart.model.enums.SUPPLY_BLOCK_STATUS;
import com.antonov.vlmart.model.enums.SUPPLY_CALL;
import com.antonov.vlmart.model.enums.SUPPLY_VOLUME;
import com.antonov.vlmart.model.place.Place;
import com.antonov.vlmart.model.place.SupplyPlace;
import com.antonov.vlmart.model.supply.SupplyBlock;
import com.antonov.vlmart.render.PlaceWrap;
import com.antonov.vlmart.render.SupplyWrap;
import com.antonov.vlmart.rest.SimpleResponse;
import com.antonov.vlmart.service.business.BusinessService;
import com.antonov.vlmart.service.notify.NotificationService;
import com.antonov.vlmart.wrap.SupplyCaller;
import com.antonov.vlmart.wrap.Unloader;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.List;
import java.util.Random;

@Service
public class SupplyServiceImpl implements SupplyService, SupplyCaller {

    private Supply supply;

    private final MartService martService;
    private final SupplyConfiguration supplyConfiguration;
    private final NotificationService notificationService;
    private final BusinessService businessService;
    private Unloader unloader;

    public SupplyServiceImpl(
            MartService martService,
            SupplyConfiguration supplyConfiguration,
            NotificationService notificationService,
            BusinessService businessService) {
        this.martService = martService;
        this.supplyConfiguration = supplyConfiguration;
        this.notificationService = notificationService;
        this.businessService = businessService;
    }

    @Override
    public void init() {
        supply = martService.getSupply();
        SUPPLY_CALL.init();
        SUPPLY_VOLUME.init();
        martService.setSupplyCaller(this);
    }

    @Override
    public SimpleResponse createSupplyBlockWrap() {
        var response = new SimpleResponse();
        if (checkExistsActiveSupply()) {
            response.setSuccess(false);
            response.setMessage("Текущая поставка не завершена");
            return response;
        }
        var block = createSupplyBlock();
        if (block == null) {
            response.setSuccess(false);
            response.setMessage("Склад заполнен. Поставка не требуется");
            return response;
        }
        notificationService.sendOnce(NOTIFY_TYPE.SUPPLY_PRICE);
        response.setMessage(String.format(
                "Создана заявка на поставку товаров в количестве %d.<br>Стоимость = %d. Ожидайте доставку товаров",
                blockSize(block),
                blockPrice(block)));
        return response;
    }

    @Override
    public boolean checkExistsActiveSupply() {
        return getActiveSupply() != null;
    }

    @Override
    public boolean checkExistsDeliveredBlock() {
        return supply
                .blocks().stream()
                .anyMatch(block -> block.status() == SUPPLY_BLOCK_STATUS.DELIVERED);
    }

    @Override
    public SupplyBlock getActiveSupply() {
        return supply
                .blocks().stream()
                .filter(block -> block.status() == SUPPLY_BLOCK_STATUS.WAIT ||
                        block.status() == SUPPLY_BLOCK_STATUS.DELIVERED)
                .findFirst()
                .orElse(null);
    }

    @Override
    public SupplyBlock getDeliveredSupply() {
        return supply
                .blocks().stream()
                .filter(block -> block.status() == SUPPLY_BLOCK_STATUS.DELIVERED)
                .findFirst()
                .orElse(null);
    }

    @Override
    public SupplyBlock getSupply() {
        return supply
                .blocks().stream()
                .findFirst()
                .orElse(null);
    }

    @Override
    public void startUnload() {
        SupplyBlock activeBlock = getDeliveredSupply();
        if (activeBlock != null) {
            activeBlock.status(SUPPLY_BLOCK_STATUS.UNLOADING);
            activeBlock.startUnloadMillis(System.currentTimeMillis());
        }
    }

    private void unloadComplete() {
        supply
                .blocks().stream()
                .filter(b -> b.status() == SUPPLY_BLOCK_STATUS.UNLOADING)
                .findFirst()
                .ifPresent(block -> {
                    for (SupplyPlace place : block.places()) {
                        martService.putToStore(place.good(), place.stock());
                    }
                    supply.blocks().remove(block);
                    callSupply(SUPPLY_CALL.CALL_RECURSIVE);
                });
    }

    @Override
    public void fillWaitingSupply(List<PlaceWrap> storePlaces) {
        var supply = getSupply();
        if (supply == null) {
            return;
        }
        storePlaces.forEach(place -> supply.places().stream()
                .filter(supplyPlace -> supplyPlace.good().equals(place.good))
                .findFirst()
                .ifPresent(supplyPlace -> place.setWaitingSupply(supplyPlace.stock)));
    }

    @Override
    public long placeCount() {
        return supply.blocks().stream()
                .mapToLong(block -> block.places().size())
                .sum();
    }

    @Override
    public void setUnloader(Unloader unloader) {
        this.unloader = unloader;
    }

    @Override
    public void loopUnload() {
        SupplyBlock block = supply
                .blocks().stream()
                .filter(b -> b.status() == SUPPLY_BLOCK_STATUS.UNLOADING &&
                        b.completeMillis() < System.currentTimeMillis())
                .findFirst()
                .orElse(null);
        if (block == null) {
            return;
        }
        unloadComplete();
        unloader.unloadComplete();
    }

    @Override
    public SupplyWrap getSupplyWrap() {
        var block = getSupply();
        if (block == null) {
            return null;
        }
        long durationSec = block.status() == SUPPLY_BLOCK_STATUS.UNLOADING ?
                (block.completeMillis() - block.startUnloadMillis()) / 1000 :
                supplyConfiguration.getDuration();
        return new SupplyWrap(block, durationSec);
    }

    private SupplyBlock createSupplyBlock() {
        int durationSec = supplyConfiguration.getDuration();
        var block = new SupplyBlock();
        var random = new Random();
        martService.storePlaces()
                .forEach(place -> {
                    int quantity;
                    if (SUPPLY_VOLUME.FULL_STORE_VOLUME.isActive()) {
                        quantity = place.capacity() - place.stock();
                    }
                    else {
                        quantity = (int) Math.round((place.capacity() - place.stock()) * random.nextDouble());
                    }

                    if (quantity > 0) {
                        block.places().add(new SupplyPlace(place.good(), quantity));
                    }
                });
        correctBlockByMoney(block);

        if (block.places().size() > 0) {
            supply.blocks().add(block);
            Mono
                    .just(block)
                    .delaySubscription(Duration.ofSeconds(durationSec))
                    .subscribeOn(Schedulers.parallel())
                    .subscribe(b -> {
                        b.status(SUPPLY_BLOCK_STATUS.DELIVERED);
                        unloader.unloadStart(false);
                        notificationService.sendOnce(NOTIFY_TYPE.SUPPLY_UNLOAD_NEEDED);
                    });

            return block;
        }
        return null;
    }

    private void correctBlockByMoney(SupplyBlock block) {
        while (blockPrice(block) > businessService.moneyCount()) {
            block.places().stream()
                    .filter(p -> p.stock > 0)
                    .forEach(p -> p.stock--);
        }
        businessService.spendMoney(blockPrice(block));
    }

    private int blockPrice(SupplyBlock block) {
        return block.places().stream()
                .mapToInt(p -> (p.stock * p.good.price() * supplyConfiguration.getCost_percent() / 1000) * 10)
                .sum();
    }

    private int blockSize(SupplyBlock block) {
        return block.places().stream()
                .mapToInt(Place::stock)
                .sum();
    }

    @Override
    public void callSupply(SUPPLY_CALL supplyCall) {
        if (!supplyCall.isActive() || !supply.blocks().isEmpty()) {
            return;
        }
        SimpleResponse response = createSupplyBlockWrap();
        notificationService.sendAlert(response);
    }
}
