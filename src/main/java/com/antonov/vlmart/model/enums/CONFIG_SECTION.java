package com.antonov.vlmart.model.enums;

import lombok.Getter;

@Getter
public enum CONFIG_SECTION {
    BUYER("Покупатели"),
    SUPPLY("Поставка товаров"),
    ASSORTMENT("Ассортимент товаров"),
    STAFF("Сотрудники");

    private final String title;

    CONFIG_SECTION(String title) {
        this.title = title;
    }
}
