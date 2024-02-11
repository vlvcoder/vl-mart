package com.antonov.vlmart.service;

import com.antonov.vlmart.render.StatusWrap;
import com.antonov.vlmart.render.SupplyPlanWrap;

public interface RootService {
    void initData();
    StatusWrap data();
    boolean checkGameOver();

    SupplyPlanWrap getSupplyPlans();
}
