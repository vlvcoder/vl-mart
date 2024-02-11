package com.antonov.vlmart.render;

import lombok.Data;

import java.util.List;

@Data
public class ConfigWrap {
    private List<ConfigSectionWrap> sections;
    private List<ConfigItemWrap> items;
}
