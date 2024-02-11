package com.antonov.vlmart.model.enums;

import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;

@Getter
public enum SUPPLY_CALL {
    CALL_MANUAL(    true,   true,   0,      "Заказ поставки делает менеджер"),
    CALL_LOW_LEWEL( false,  false,  1_000_000, "Автоматический заказ по достижении низкого уровня одного из товаров на складе"),
    CALL_RECURSIVE( false,  false,  2_000_000, "Автоматический заказ по завершению разгрузки");

    @Setter
    private boolean enabled;
    private boolean active;
    private final int price;
    private final String title;

    SUPPLY_CALL(boolean enabled, boolean active, int price, String title) {
        this.enabled = enabled;
        this.active = active;
        this.price = price;
        this.title = title;
    }

    public static void init() {
        CALL_MANUAL.active = CALL_MANUAL.enabled = true;
        CALL_RECURSIVE.active = CALL_RECURSIVE.enabled = CALL_LOW_LEWEL.active = CALL_LOW_LEWEL.enabled = false;
    }

    public static SUPPLY_CALL activeCall() {
        return Arrays.stream(SUPPLY_CALL.values())
                .filter(s -> s.active)
                .findFirst()
                .orElse(null);
    }

    public SUPPLY_CALL turnActive() {
        if (active) {
            return this;
        }
        if (!enabled) {
            return activeCall();
        }
        activeCall().active = false;
        active = true;
        return this;
    }

}
