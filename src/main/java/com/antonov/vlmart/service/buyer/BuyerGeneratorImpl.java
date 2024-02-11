package com.antonov.vlmart.service.buyer;

import com.antonov.vlmart.config.BuyerConfiguration;
import com.antonov.vlmart.model.buyer.Buyer;
import com.antonov.vlmart.model.buyer.Purchase;
import com.antonov.vlmart.model.enums.GOOD;
import com.antonov.vlmart.service.business.BusinessService;
import com.antonov.vlmart.service.product.MartService;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class BuyerGeneratorImpl implements BuyerGenerator {
    private final BuyerConfiguration buyerConfiguration;
    private final MartService martService;
    private final BusinessService businessService;

    private long nextBuyerMillis;

    private int income_interval_sec_max;
    private int unit_count_max;

    private int speedUp = 0;

    public BuyerGeneratorImpl(
            BuyerConfiguration buyerConfiguration,
            MartService martService,
            BusinessService businessService) {
        this.buyerConfiguration = buyerConfiguration;
        this.martService = martService;
        this.businessService = businessService;
        init();
    }

    @Override
    public void init() {
        income_interval_sec_max = buyerConfiguration.getIncome_interval_sec_max();
        unit_count_max = buyerConfiguration.getUnit_count_max();
        speedUp = 0;
    }

    @Override
    public void generateBuyer() {
        if (martService.opened() && System.currentTimeMillis() > nextBuyerMillis) {
            createBuyer();
            wantNextBuyer();
            setNextInterval();
        }
    }

    @Override
    public int getIncome_interval_sec_max() {
        return income_interval_sec_max;
    }

    @Override
    public int getUnit_count_max() {
        return unit_count_max;
    }

    private void setNextInterval() {
        int crnt = businessService.likeCount() / buyerConfiguration.getSpeedup_like_level();
        if (crnt > speedUp) {
            if (income_interval_sec_max > buyerConfiguration.getIncome_interval_sec_min()) {
                income_interval_sec_max--;
            }
            speedUp = crnt;
        }

        crnt = businessService.likeCount() / (buyerConfiguration.getUnit_speedup_like_level());
        crnt = Math.min(crnt, buyerConfiguration.getMax_purchase_count());
        unit_count_max = buyerConfiguration.getUnit_count_max() + crnt;
    }

    private void wantNextBuyer() {
        var min = buyerConfiguration.getIncome_interval_sec_min();
        var max = income_interval_sec_max;
        var rnd = new Random().nextDouble();
        var intervalSec = min + Math.round((max - min) * rnd);
        nextBuyerMillis = System.currentTimeMillis() + intervalSec * 1000L;
    }

    private void createBuyer() {
        var buyer = new Buyer();
        buyer.purchases().addAll(generatePurchaseList());
        martService.getHall().buyers().add(buyer);
    }

    private List<Purchase> generatePurchaseList() {
        List<Purchase> list = new ArrayList<>();
        List<GOOD> goods = martService.getPresentedGoods();
        Collections.shuffle(goods);
        var random = new Random();
        int positionCount = 1 +
                (int) Math.round((goods.size() - 1) * random.nextDouble());

        for (int i = 0; i < positionCount; i++) {
            var countMin = 1;
            int unitCount = countMin +
                    (int) Math.round((unit_count_max - countMin) * random.nextDouble());

            var tryMin = buyerConfiguration.getTry_count_min();
            var tryMax = buyerConfiguration.getTry_count_max();
            int tryCount = tryMin +
                    (int) Math.round((tryMax - tryMin) * random.nextDouble());

            var purchase = new Purchase(goods.get(i), unitCount, tryCount);
            list.add(purchase);
        }

        return list;
    }
}
