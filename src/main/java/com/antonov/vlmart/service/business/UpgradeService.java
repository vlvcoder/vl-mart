package com.antonov.vlmart.service.business;

import com.antonov.vlmart.model.enums.*;
import com.antonov.vlmart.render.ExpandWrap;
import com.antonov.vlmart.render.RaiseWrap;
import com.antonov.vlmart.render.UpgradeWrap;
import com.antonov.vlmart.rest.SimpleResponse;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface UpgradeService {
    List<RaiseWrap> fillRaising();
    SimpleResponse fillRaise(int quantity);

    List<RaiseWrap> cashRaising();
    SimpleResponse cashRaise(int quantity);

    SimpleResponse addGood(@RequestParam GOOD good);

    SimpleResponse addStaff(@RequestParam STAFF_ROLE role);

    ExpandWrap expandPrices();

    SimpleResponse expandPlace(SPACE_TYPE spaceType, GOOD good, PLACE_VOLUME volume);

    SimpleResponse setSupplyPlan(SUPPLY_CALL call, SUPPLY_VOLUME volume);

    SimpleResponse decreaseRestDuration(@RequestParam STAFF_ROLE role, int value);

    UpgradeWrap currentUpgrade();
}
