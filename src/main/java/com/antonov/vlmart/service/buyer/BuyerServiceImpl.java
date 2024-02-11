package com.antonov.vlmart.service.buyer;

import com.antonov.vlmart.config.BuyerConfiguration;
import com.antonov.vlmart.config.MainConfiguration;
import com.antonov.vlmart.model.area.Hall;
import com.antonov.vlmart.model.buyer.Buyer;
import com.antonov.vlmart.model.buyer.Purchase;
import com.antonov.vlmart.model.enums.BUYER_STATUS;
import com.antonov.vlmart.model.enums.ESTIMATION_TYPE;
import com.antonov.vlmart.model.enums.NOTIFY_TYPE;
import com.antonov.vlmart.render.MediatorWrap;
import com.antonov.vlmart.service.business.BusinessService;
import com.antonov.vlmart.service.notify.NotificationService;
import com.antonov.vlmart.service.product.MartService;
import com.antonov.vlmart.wrap.BuyerInCash;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class BuyerServiceImpl implements BuyerService {

    private final BuyerConfiguration buyerConfiguration;
    private final MainConfiguration mainConfiguration;
    private final MartService martService;
    private final BuyerGenerator buyerGenerator;
    private final BusinessService businessService;
    private final NotificationService notificationService;

    private Hall hall;

    public BuyerServiceImpl(
            BuyerConfiguration buyerConfiguration,
            MainConfiguration mainConfiguration,
            MartService martService,
            BuyerGenerator buyerGenerator,
            BusinessService businessService,
            NotificationService notificationService) {
        this.buyerConfiguration = buyerConfiguration;
        this.mainConfiguration = mainConfiguration;
        this.martService = martService;
        this.buyerGenerator = buyerGenerator;
        this.businessService = businessService;
        this.notificationService = notificationService;
    }

    @Override
    public void init() {
        hall = martService.getHall();
        buyerGenerator.init();
    }

    @Override
    public void loopBuyers() {
        buyerGenerator.generateBuyer();
        processHall();
        processCash();

        //todo Для отладки - закрывать магазин после каждого покупателя
//        martService.close();

    }

    private void processCash() {
        hall.buyers().stream()
                .filter(buyer ->
                        buyer.status() == BUYER_STATUS.CASH_WAIT &&
                                System.currentTimeMillis() > buyer.nextStepMillis())
                .forEach(buyer -> {
                    setCashTreshhold(buyer);
                    businessService.like(ESTIMATION_TYPE.LONG_CASH_HANDLE);
                });
    }

    @Override
    public synchronized BuyerInCash inviteBuyerToCash() {
        return hall.buyers().stream()
                .filter(buyer -> buyer.status() == BUYER_STATUS.CASH_WAIT)
                .min(Comparator.comparingLong(Buyer::nextStepMillis))
                .map(buyer -> {
                    buyer.status(BUYER_STATUS.CASH);
                    int unitCount = buyerUnitCount(buyer);
                    var cashDurationSec = unitCount * buyerConfiguration.getPosition_duration_cash();
                    return new BuyerInCash(buyer.id(), cashDurationSec, unitCount);
                })
                .orElse(null);
    }

    private void checkLongQueue() {
        long count = hall.buyers().stream()
                .filter(buyer -> buyer.status() == BUYER_STATUS.CASH_WAIT)
                .count();
        if (count > buyerConfiguration.getCash_queue_treshhold()) {
            notificationService.sendOnce(NOTIFY_TYPE.LONG_CASH_QUEUE);
        } else if (count > 0) {
            notificationService.sendOnce(NOTIFY_TYPE.FIRST_BUYER_IN_CASH);
        }
    }

    private static int buyerUnitCount(Buyer buyer) {
        return buyer.purchases().stream()
                .mapToInt(Purchase::fact)
                .sum();
    }

    @Override
    public synchronized void fixBuyerInCash(String buyerId) {
        hall.buyers().stream()
                .filter(buyer -> buyer.id().equals(buyerId))
                .findFirst()
                .ifPresent(buyer -> {
                    int money = buyerUnitCost(buyer);
                    money -= money * buyerConfiguration.getVat_percent() / 100;
                    businessService.addMoney(money);
                    if (allPurchasesComplete(buyer)) {
                        businessService.like(ESTIMATION_TYPE.WELL_SHOPPING);
                    } else {
                        businessService.like(ESTIMATION_TYPE.ABSENT_POSITIONS);
                    }
                    hall.buyers().remove(buyer);
                });
    }

    @Override
    public synchronized void cancelLongBuyers() {
        hall.buyers().removeIf(buyer ->
                (System.currentTimeMillis() - buyer.nextStepMillis()) >
                        (mainConfiguration.getCancel_long_objects_interval() * 1000L));
    }

    @Override
    public Map<BUYER_STATUS, Long> buyersStates() {
        return hall.buyers().stream()
                .collect(Collectors.groupingBy(Buyer::status, Collectors.counting()));
    }

    @Override
    public void fillBuyerNeeds(List<MediatorWrap> mediatorWraps) {
        List<Buyer> buyers = hall.buyers().stream()
                .filter(b -> b.status() == BUYER_STATUS.BUY)
                .collect(Collectors.toList());
        for (MediatorWrap mw : mediatorWraps) {
            int sum = buyers.stream()
                    .mapToInt(buyer -> buyer.purchases().stream()
                            .filter(p -> p.good() == mw.getGood())
                            .mapToInt(p -> p.plan() - p.fact()).sum())
                    .sum();
            mw.setBuyersNeeds(sum);
        }
    }

    @Override
    public long buyerCount() {
        return hall.buyers().size();
    }

    @Override
    public long purchaseCount() {
        return hall.buyers().stream()
                .mapToLong(buyer -> buyer.purchases().size())
                .sum();
    }

    private boolean allPurchasesComplete(Buyer buyer) {
        return buyer.purchases().stream()
                .noneMatch(p -> p.fact() < p.plan());
    }

    private int buyerUnitCost(Buyer buyer) {
        return buyer.purchases().stream()
                .mapToInt(purchase -> purchase.good().price() * purchase.fact())
                .sum();
    }

    private synchronized void processHall() {
        var readyBuyers = hall.buyers().stream()
                .filter(buyer -> buyer.status() == BUYER_STATUS.BUY &&
                        System.currentTimeMillis() > buyer.nextStepMillis())
                .collect(Collectors.toList());
        readyBuyers
                .forEach(buyer -> {
                    if (!makePurchases(buyer)) {
                        if (buyerUnitCount(buyer) > 0) {
                            buyer.status(BUYER_STATUS.CASH_WAIT);
                            checkLongQueue();
                            setCashTreshhold(buyer);
                            unlikeLongQueue();
                        } else {
                            businessService.like(ESTIMATION_TYPE.ABSENT_ALL_POSITIONS);
                            hall.buyers().remove(buyer);
                        }
                    }
                });
    }

    private void unlikeLongQueue() {
        long countInQueue = hall.buyers().stream().filter(buyer -> buyer.status() == BUYER_STATUS.CASH_WAIT).count();
        if (countInQueue > buyerConfiguration.getCash_queue_treshhold()) {
            businessService.like(ESTIMATION_TYPE.QUEUE_IN_CASH);
        }
    }

    private void setCashTreshhold(Buyer buyer) {
        long next = System.currentTimeMillis() + buyerConfiguration.getCash_handle_duration_treshhold() * 1000L;
        buyer.nextStepMillis(next);
    }

    private boolean makePurchases(Buyer buyer) {
        var purchase = buyer.purchases().stream()
                .filter(p -> p.tryCount() > 0 && p.fact() < p.plan())
                .findFirst()
                .orElse(null);
        if (purchase == null) {
            return false;
        }
        int take = martService.takeFromShop(purchase.good(), purchase.plan() - purchase.fact());
        purchase.fact(purchase.fact() + take);
        if (purchase.fact() < purchase.plan()) {
            purchase.tryCount(purchase.tryCount() - 1);
        }
        buyer.nextStepMillis(System.currentTimeMillis() + buyerConfiguration.getPosition_duration_shelf() * 1000L);
        return true;
    }
}
