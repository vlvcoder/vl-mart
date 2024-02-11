package com.antonov.vlmart.model.enums;

import lombok.Getter;

public enum FILL_LEVEL {
    LOW(20),
    NORMAL(80),
    HIGH(100);

    @Getter
    private final int level;

    FILL_LEVEL(int level) {
        this.level = level;
    }
}
