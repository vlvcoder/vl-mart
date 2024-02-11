package com.antonov.vlmart.render;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpgradeWrap {
    private String addGoodPrice;
    private int cashierRestDuration;
    private int shelverRestDuration;
    private String restDecreasePrice_5;
    private String restDecreasePrice_10;
}
