package com.antonov.vlmart.service.business;

import com.antonov.vlmart.model.area.Business;
import com.antonov.vlmart.model.enums.ESTIMATION_TYPE;
import com.antonov.vlmart.model.enums.NOTIFY_TYPE;
import com.antonov.vlmart.model.enums.STAFF_ROLE;
import com.antonov.vlmart.render.BusinessWrap;
import com.antonov.vlmart.service.notify.NotificationService;
import com.antonov.vlmart.service.product.MartService;
import com.antonov.vlmart.utils.Utils;
import com.antonov.vlmart.wrap.Liker;
import org.springframework.stereotype.Service;

@Service
public class BusinessServiceImpl implements BusinessService, Liker {
    private final MartService martService;
    private final NotificationService notificationService;
    private Business business;

    public BusinessServiceImpl(
            MartService martService,
            NotificationService notificationService) {
        this.martService = martService;
        this.notificationService = notificationService;
    }

    @Override
    public void init() {
        business = new Business();
        martService.setLiker(this);
    }

    @Override
    public long moneyCount() {
        return business == null ? 0 : business.money();
    }

    @Override
    public int likeCount() {
        return business == null ? 0 : business.like();
    }

    @Override
    public int dislikeCount() {
        return business == null ? 0 : business.dislike();
    }

    @Override
    public synchronized void addMoney(long count) {
        business.money(business.money() + count);
        checkBuyStaff();
    }

    private void checkBuyStaff() {
        if (business.money() >= STAFF_ROLE.CASHIER.cost()) {
            notificationService.sendOnce(NOTIFY_TYPE.CAN_BUY_CAHIER);
        }
        if (business.money() >= STAFF_ROLE.SHELVER.cost()) {
            notificationService.sendOnce(NOTIFY_TYPE.CAN_BUY_SHELVER);
        }
        if (business.money() >= STAFF_ROLE.LOADER.cost()) {
            notificationService.sendOnce(NOTIFY_TYPE.CAN_BUY_LOADER);
        }
    }

    @Override
    public synchronized boolean spendMoney(long count) {
        if (business.money() < count) {
            return false;
        }
        business.money(business.money() - count);
        return true;
    }

    @Override
    public void like(ESTIMATION_TYPE estimationType) {
        if (estimationType.isPositive()) {
            business.like(business.like() + estimationType.value());
            notificationService.sendOnce(NOTIFY_TYPE.FIRST_LIKE);
        } else {
            business.dislike(business.dislike() + estimationType.value());
            if (estimationType == ESTIMATION_TYPE.ABSENT_POSITIONS) {
                notificationService.sendOnce(NOTIFY_TYPE.DISLIKE_ABSENT_POSITIONS);
            }
        }
        martService.moneyChanged(business.like());
    }

    @Override
    public boolean reachMillion() {
        return business.reachMillion();
    }

    @Override
    public void setReachMillion(boolean value) {
        business.reachMillion(value);
    }

    @Override
    public String statusText() {
        return business.statusText();
    }

    @Override
    public BusinessWrap businessWrap() {
        return BusinessWrap.builder()
                .moneyCount(business.money())
                .money(Utils.longNumberFormat(business.money()))
                .like(Utils.longNumberFormat(business.like()))
                .dislike(Utils.longNumberFormat(business.dislike()))
                .build();
    }

}
