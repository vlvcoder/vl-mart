package com.antonov.vlmart.service.product;

import com.antonov.vlmart.model.supply.SupplyBlock;
import com.antonov.vlmart.render.PlaceWrap;
import com.antonov.vlmart.render.SupplyWrap;
import com.antonov.vlmart.rest.SimpleResponse;
import com.antonov.vlmart.wrap.Unloader;

import java.util.List;

public interface SupplyService {
    void init();
    SimpleResponse createSupplyBlockWrap();
    boolean checkExistsActiveSupply();

    boolean checkExistsDeliveredBlock();

    SupplyBlock getActiveSupply();
    SupplyBlock getDeliveredSupply();
    SupplyBlock getSupply();
    void startUnload();

    void fillWaitingSupply(List<PlaceWrap> storePlaces);

    long placeCount();

    void setUnloader(Unloader unloader);

    void loopUnload();

    SupplyWrap getSupplyWrap();
}
