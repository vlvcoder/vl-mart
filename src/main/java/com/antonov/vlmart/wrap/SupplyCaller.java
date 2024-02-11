package com.antonov.vlmart.wrap;

import com.antonov.vlmart.model.enums.SUPPLY_CALL;

public interface SupplyCaller {
    void callSupply(SUPPLY_CALL supplyCall);
}
