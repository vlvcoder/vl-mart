package com.antonov.vlmart.model.enums;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
public enum STAFF_ROLE {
    MANAGER("Менеджер", 8, 0, 1),
    CASHIER("Кассир", 1, 10_000, 2),
    SHELVER("Мерчендайзер", 4, 20_000, 3),
    LOADER("Грузчик", 4, 25_000, 4),
    COOK("Повар", 1, 12_000, 5);

    private final String title;
    @Setter
    private int power;
    private final int cost;
    private final int order;

    STAFF_ROLE(String title, int power, int cost, int order) {
        this.title = title;
        this.power = power;
        this.cost = cost;
        this.order = order;
    }
}
