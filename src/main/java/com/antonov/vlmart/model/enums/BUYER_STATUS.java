package com.antonov.vlmart.model.enums;

import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
public enum BUYER_STATUS {
    BUY("Выбор товаров", 1),
    CASH_WAIT("Очередь в кассу", 2),
    CASH("Пробитие товаров", 3);

    BUYER_STATUS(String title, int order) {
        this.title = title;
        this.order = order;
    }

    private final String title;
    private final int order;
}
