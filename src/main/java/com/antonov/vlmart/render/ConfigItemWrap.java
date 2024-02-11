package com.antonov.vlmart.render;

import com.antonov.vlmart.model.enums.CONFIG_SECTION;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ConfigItemWrap {
    private String name;
    private String title;
    private CONFIG_SECTION section;
    private long current;
    private long max;
    private int increaseLikesStep;
}
