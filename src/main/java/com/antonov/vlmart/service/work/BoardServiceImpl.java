package com.antonov.vlmart.service.work;

import com.antonov.vlmart.config.MainConfiguration;
import com.antonov.vlmart.config.QuantConfiguration;
import com.antonov.vlmart.model.area.Board;
import com.antonov.vlmart.model.enums.GOOD;
import com.antonov.vlmart.model.enums.QUANT_TYPE;
import com.antonov.vlmart.model.enums.STAFF_ROLE;
import com.antonov.vlmart.model.enums.SUPPLY_BLOCK_STATUS;
import com.antonov.vlmart.model.place.Place;
import com.antonov.vlmart.model.quant.CashCheckoutQuant;
import com.antonov.vlmart.model.quant.PlacementQuant;
import com.antonov.vlmart.model.quant.Quant;
import com.antonov.vlmart.model.quant.UnloadQuant;
import com.antonov.vlmart.model.staff.Staff;
import com.antonov.vlmart.render.MediatorWrap;
import com.antonov.vlmart.render.QuantWrap;
import com.antonov.vlmart.service.buyer.BuyerService;
import com.antonov.vlmart.service.product.MartService;
import com.antonov.vlmart.service.product.SupplyService;
import com.antonov.vlmart.wrap.BuyerInCash;
import com.antonov.vlmart.wrap.QuantCompleter;
import com.antonov.vlmart.wrap.Unloader;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BoardServiceImpl implements BoardService, Unloader {

    private final MainConfiguration mainConfiguration;
    private final QuantConfiguration quantConfiguration;
    private final MartService martService;
    private final SupplyService supplyService;
    private final BuyerService buyerService;

    private Board board;
    private QuantCompleter quantCompleter;


    public BoardServiceImpl(
            MainConfiguration mainConfiguration,
            QuantConfiguration quantConfiguration,
            MartService martService,
            SupplyService supplyService,
            BuyerService buyerService) {
        this.mainConfiguration = mainConfiguration;
        this.quantConfiguration = quantConfiguration;
        this.martService = martService;
        this.supplyService = supplyService;
        this.buyerService = buyerService;
    }

    @Override
    public void init() {
        board = martService.getBoard();
        supplyService.setUnloader(this);
    }

    @Override
    public Quant createPlacementQuant(Staff staff, GOOD good) {
        Place targetPlace = martService.getShopPlace(good);
        if (targetPlace == null) {
            return null;
        }
        var quantity = Math.min(staff.role().power(), (targetPlace.capacity() - targetPlace.stock() - lockedQuant(good)));
        quantity = martService.takeFromStore(good, quantity);
        if (quantity == 0) {
            return null;
        }

        PlacementQuant quant = new PlacementQuant(
                QUANT_TYPE.PLACEMENT,
                staff,
                good,
                quantity);
        return acceptQueue(quant, quantConfiguration.getPlacement());
    }

    @Override
    public Quant createCashCheckoutQuant(Staff staff) {
        BuyerInCash buyerInCash = buyerService.inviteBuyerToCash();
        if (buyerInCash == null) {
            return null;
        }
        int duration = buyerInCash.duration() / STAFF_ROLE.CASHIER.power();
        var quant = new CashCheckoutQuant(
                QUANT_TYPE.CASH_CHECKOUT,
                staff,
                buyerInCash.id(),
                buyerInCash.unitCount());
        return acceptQueue(quant, duration);
    }

    private Quant acceptQueue(Quant quant, int duration) {
        quantCompleter.quantStart(quant.staff());
        board.quants().add(quant);
        if (duration > 0) {
            wantComplete(quant, duration);
        }
        return quant;
    }

    private void wantComplete(Quant quant, int duration) {
        quant.durationSec(duration);
        Mono
                .just(quant)
                .delaySubscription(Duration.ofSeconds(duration))
                .subscribeOn(Schedulers.parallel())
                .subscribe(this::quantCompleteDispatch);
    }

    @Override
    public void setQuantCompleter(QuantCompleter completer) {
        this.quantCompleter = completer;
    }

    @Override
    public synchronized int lockedQuant(GOOD good) {
        return board.quants().stream()
                .filter(quant -> quant.quantType() == QUANT_TYPE.PLACEMENT)
                .map(quant -> (PlacementQuant) quant)
                .filter(quant -> quant.good() == good)
                .mapToInt(PlacementQuant::quantity)
                .sum();
    }

    @Override
    public void cancelLongQuants() {
        board.quants().removeIf(quant ->
                (System.currentTimeMillis() - quant.startMillis()) > (mainConfiguration.getCancel_long_objects_interval() * 1000L));
    }

    @Override
    public void fillMovings(List<MediatorWrap> mediatorWraps) {
        board.quants().stream()
                .filter(quant -> quant instanceof PlacementQuant)
                .map(quant -> (PlacementQuant) quant)
                .forEach(quant -> mediatorWraps.stream()
                        .filter(mw -> mw.getGood() == quant.good())
                        .findFirst()
                        .ifPresent(mw -> mw.setMovings(mw.getMovings() + quant.quantity())));
    }

    @Override
    public List<QuantWrap> activeQuants() {
        return board.quants().stream()
                .map(QuantWrap::new)
                .collect(Collectors.toList());
    }

    @Override
    public long quantCount() {
        return board.quants().size();
    }

    @Override
    public List<Quant> quants() {
        return board.quants();
    }

    private synchronized void quantCompleteDispatch(Quant quant) {
        switch (quant.quantType()) {
            case PLACEMENT:
                placementQuantComplete((PlacementQuant) quant);
                break;
            case CASH_CHECKOUT:
                cashCheckoutQuantComplete((CashCheckoutQuant) quant);
                break;
        }
    }

    private void quantComplete(Quant quant) {
        quantCompleter.quantComplete(quant.staff());
        board.quants().remove(quant);
    }

    private void cashCheckoutQuantComplete(CashCheckoutQuant quant) {
        buyerService.fixBuyerInCash(quant.buyerId());
        quantComplete(quant);
    }

    private void placementQuantComplete(PlacementQuant quant) {
        int reserve = quant.quantity() - quant.quantity_completed();
        quant.quantity_completed(
                quant.quantity_completed() + martService.putToShop(quant.good(), reserve)
        );
        if (quant.quantity_completed() == quant.quantity()) {
            quantComplete(quant);
        } else {
            int duration = (int) (System.currentTimeMillis() - quant.startMillis());
            wantComplete(quant, duration);
        }
    }

    @Override
    public synchronized boolean unloadStart(boolean includeManager) {
        var supply = supplyService.getSupply();
        var loaders = quantCompleter.staffs(STAFF_ROLE.LOADER);
        if (includeManager) {
            loaders.addAll(quantCompleter.staffs(STAFF_ROLE.MANAGER));
        }
        if (supply == null || supply.status() == SUPPLY_BLOCK_STATUS.WAIT || loaders.isEmpty()) {
            return false;
        }
        var goodsCount = supply
                .places().stream()
                .mapToInt(Place::stock).sum();
        var speed = loaders.stream()
                .mapToInt(staff -> staff.role().power())
                .sum();
        var now = System.currentTimeMillis();
        float completePart = supply.status() == SUPPLY_BLOCK_STATUS.DELIVERED ?
                0 :
                (float) (now - supply.startUnloadMillis()) / (float) (supply.completeMillis() - supply.startUnloadMillis());


        int constant = quantConfiguration.getUnload_constant();
        int coeff = quantConfiguration.getUnload_coeff();
        int duration = Math.round((constant + (goodsCount / speed / coeff)) * (1 - completePart));

        if (supply.status() == SUPPLY_BLOCK_STATUS.DELIVERED) {
            supplyService.startUnload();
        }
        supply.completeMillis(now + duration * 1000L);

        board.quants().removeIf(quant -> loaders.contains(quant.staff()));
        loaders.forEach(staff -> {
            var quant = new UnloadQuant(staff, goodsCount);
            acceptQueue(quant, 0);
        });
        return true;
    }

    @Override
    public synchronized void unloadComplete() {
        var list = board.quants().stream()
                .filter(quant -> quant.quantType() == QUANT_TYPE.UNLOAD)
                .collect(Collectors.toList());
        for (Quant quant : list) {
            quantComplete(quant);
        }
    }
}
