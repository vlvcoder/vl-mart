package com.antonov.vlmart.model.enums;

import lombok.Getter;

public enum PLACE_VOLUME {
    SMALL(1),
    MEDIUM(1.5),
    LARGE(2.5);

    @Getter
    private final double expandCostCoeff;

    PLACE_VOLUME(double expandCostCoeff) {
        this.expandCostCoeff = expandCostCoeff;
    }
}
