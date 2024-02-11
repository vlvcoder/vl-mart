package com.antonov.vlmart.render;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class SupplyPlanWrap {
    private final List<SupplyCallWrap> supplyCalls;
    private final List<SupplyVolumeWrap> supplyVolumes;
}
