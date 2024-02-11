package com.antonov.vlmart.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "vlmart.supply")
public class SupplyConfiguration {
    private int duration;
    private int cost_percent;
}
