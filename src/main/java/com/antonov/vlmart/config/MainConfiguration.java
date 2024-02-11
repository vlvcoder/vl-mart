package com.antonov.vlmart.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "vlmart.main")
public class MainConfiguration {
    private boolean store_full;
    private boolean shop_full;
    private int cancel_long_objects_interval;
    private int add_good_price;

    private int good_group_update_likes;
    private int gameover_dislike_count;
}
