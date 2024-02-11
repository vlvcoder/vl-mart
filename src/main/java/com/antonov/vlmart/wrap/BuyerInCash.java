package com.antonov.vlmart.wrap;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
@AllArgsConstructor
public class BuyerInCash {
    private final String id;
    private final int duration;
    private final int unitCount;
}
