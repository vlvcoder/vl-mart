package com.antonov.vlmart.model.enums;


import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
public enum ESTIMATION_TYPE {
    WELL_SHOPPING("Отличный шопинг", true, 1),
    ABSENT_POSITIONS("Отсутствуют некоторые товары", false, 1),
    ABSENT_ALL_POSITIONS("Отсутствуют все товары", false, 2),
    QUEUE_IN_CASH("Большая очередь в кассу", false, 1),
    LONG_CASH_HANDLE("Долгое обслуживание на кассе", false, 1),
    CLOSED("Магазин закрыт", false, 5);

    ESTIMATION_TYPE(String title, boolean isPositive, int value) {
        this.title = title;
        this.isPositive = isPositive;
        this.value = value;
    }

    private final String title;
    private final boolean isPositive;
    private final int value;
}
