package com.antonov.vlmart.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "vlmart.buyer")
public class BuyerConfiguration {
    private int position_duration_shelf;
    private int position_duration_cash;

    private int income_interval_sec_min;
    private int income_interval_sec_max;
    private int speedup_like_level;

    private int unit_count_max;
    private int unit_speedup_like_level;

    private int try_count_min;
    private int try_count_max;

    private int cash_queue_treshhold;
    private int cash_handle_duration_treshhold;
    private int max_purchase_count;
    private int vat_percent;

}
