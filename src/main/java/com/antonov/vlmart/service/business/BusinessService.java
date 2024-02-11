package com.antonov.vlmart.service.business;

import com.antonov.vlmart.model.enums.ESTIMATION_TYPE;
import com.antonov.vlmart.render.BusinessWrap;

public interface BusinessService {
    void init();
    long moneyCount();
    int likeCount();
    int dislikeCount();
    void addMoney(long count);
    boolean spendMoney(long count);
    void like(ESTIMATION_TYPE estimationType);
    boolean reachMillion();
    void setReachMillion(boolean value);

    BusinessWrap businessWrap();

    String statusText();

}
