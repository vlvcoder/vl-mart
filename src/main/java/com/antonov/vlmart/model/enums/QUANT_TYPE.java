package com.antonov.vlmart.model.enums;

import lombok.Getter;

public enum QUANT_TYPE {
    UNLOAD("Приемка товара"),
    PLACEMENT("Размещение товара"),
    CASH_CHECKOUT("Пробитие на кассе");

    @Getter
    private String title;

    QUANT_TYPE(String title) {
        this.title = title;
    }
}
