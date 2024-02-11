package com.antonov.vlmart.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "vlmart.quant.duration")
public class QuantConfiguration {
    private int unload_constant;
    private int unload_coeff;
    private int placement;
}
