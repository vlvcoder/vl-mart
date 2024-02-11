package com.antonov.vlmart.model.enums;

import lombok.Getter;

public enum SUPPLY_BLOCK_STATUS {
    WAIT("Поставка"),
    DELIVERED("Доставлено"),
    UNLOADING("Разгрузка");

    SUPPLY_BLOCK_STATUS(String title) {
        this.title = title;
    }

    @Getter
    private final String title;
}
