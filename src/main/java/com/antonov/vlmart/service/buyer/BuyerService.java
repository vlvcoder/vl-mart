package com.antonov.vlmart.service.buyer;

import com.antonov.vlmart.model.enums.BUYER_STATUS;
import com.antonov.vlmart.render.MediatorWrap;
import com.antonov.vlmart.wrap.BuyerInCash;

import java.util.List;
import java.util.Map;

public interface BuyerService {
    void init();
    void loopBuyers();
    BuyerInCash inviteBuyerToCash();

    void fixBuyerInCash(String buyerId);

    void cancelLongBuyers();

    Map<BUYER_STATUS, Long> buyersStates();

    void fillBuyerNeeds(List<MediatorWrap> mediatorWraps);

    long buyerCount();

    long purchaseCount();
}
