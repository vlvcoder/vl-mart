package com.antonov.vlmart.model.enums;

import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;

@Getter
public enum SUPPLY_VOLUME {
    PROVIDER_VOLUME(true, true, 0, "Количество товаров определяет поставщик"),
    FULL_STORE_VOLUME(false, false, 1_000_000, "Количество товаров до полного заполнение склада");

    @Setter
    private boolean enabled;
    private boolean active;
    private final int price;
    private final String title;

    SUPPLY_VOLUME(boolean enabled, boolean active, int price, String title) {
        this.enabled = enabled;
        this.active = active;
        this.price = price;
        this.title = title;
    }

    public static void init() {
        PROVIDER_VOLUME.active = PROVIDER_VOLUME.enabled = true;
        FULL_STORE_VOLUME.active = FULL_STORE_VOLUME.enabled = false;
    }

    public static SUPPLY_VOLUME activeVolume() {
        return Arrays.stream(SUPPLY_VOLUME.values())
                .filter(v -> v.active)
                .findFirst()
                .orElse(null);
    }

    public SUPPLY_VOLUME turnActive() {
        if (active) {
            return this;
        }
        if (!enabled) {
            return activeVolume();
        }
        activeVolume().active = false;
        active = true;
        return this;
    }

}
