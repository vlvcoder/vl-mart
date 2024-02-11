package com.antonov.vlmart.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "vlmart.staff.power")
public class StaffConfiguration {
    private int cashier_rest_duration;
    private int shelver_rest_duration;
    private int rest_duration_decrease_price_5;
    private int rest_duration_decrease_price_10;

    private int fill_raising_price_4;
    private int fill_raising_price_8;
    private int fill_raising_price_16;

    private int cash_raising_price_1;
    private int cash_raising_price_3;

    private int manager_power_max;
    private int shelver_power_max;
    private int loader_power_max;
    private int cashier_power_max;

}
