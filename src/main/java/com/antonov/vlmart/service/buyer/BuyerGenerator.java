package com.antonov.vlmart.service.buyer;

public interface BuyerGenerator {
    void init();
    void generateBuyer();

    int getIncome_interval_sec_max();
    int getUnit_count_max();
}
