package com.antonov.vlmart.model.enums;

import lombok.Getter;
import lombok.experimental.Accessors;

// icons:
// https://icons8.com/icons/set/bread
@Getter
@Accessors(fluent = true)
public enum GOOD {
    BREAD(  0, "Хлеб",   40,     PLACE_VOLUME.LARGE,     PLACE_VOLUME.LARGE),
    MILK(   0, "Молоко", 90,     PLACE_VOLUME.LARGE,     PLACE_VOLUME.LARGE),
    CHEESE( 0, "Сыр",    320,    PLACE_VOLUME.MEDIUM,    PLACE_VOLUME.MEDIUM),
    EGGS(   0, "Яйца",   120,    PLACE_VOLUME.MEDIUM,    PLACE_VOLUME.MEDIUM),
    SAUSAGE(0, "Колбаса", 260,   PLACE_VOLUME.MEDIUM,    PLACE_VOLUME.SMALL),

    HAM(    1, "Ветчина", 380,  PLACE_VOLUME.SMALL,     PLACE_VOLUME.SMALL),
    YOGURT( 1, "Йогурт", 30,    PLACE_VOLUME.MEDIUM,    PLACE_VOLUME.MEDIUM),
    PASTA(  1, "Макароны", 60,  PLACE_VOLUME.LARGE,     PLACE_VOLUME.MEDIUM),
    TEA(    1, "Чай", 110,       PLACE_VOLUME.MEDIUM,    PLACE_VOLUME.LARGE),
    COFFEE( 1, "Кофе", 420,      PLACE_VOLUME.MEDIUM,    PLACE_VOLUME.MEDIUM),

    BUTTER( 2, "Масло", 230,      PLACE_VOLUME.MEDIUM,    PLACE_VOLUME.MEDIUM),
    RICE( 2, "Рис", 120,      PLACE_VOLUME.MEDIUM,    PLACE_VOLUME.MEDIUM),
    COOKIE( 2, "Печенье", 230,      PLACE_VOLUME.MEDIUM,    PLACE_VOLUME.MEDIUM),
    FISH( 2, "Рыба", 460,      PLACE_VOLUME.MEDIUM,    PLACE_VOLUME.MEDIUM),
    CAVIAR( 2, "Икра", 950,      PLACE_VOLUME.SMALL,    PLACE_VOLUME.SMALL),

    ORANGE( -1, "Апельсины", 270,   PLACE_VOLUME.MEDIUM, PLACE_VOLUME.LARGE),
    APPLE( -1, "Яблоки", 160,       PLACE_VOLUME.LARGE,    PLACE_VOLUME.LARGE),
    GRAPE( -1, "Виноград", 320,     PLACE_VOLUME.LARGE,    PLACE_VOLUME.LARGE),
    LEMON( -1, "Лимоны", 220,       PLACE_VOLUME.LARGE,    PLACE_VOLUME.LARGE),
    BANANA( -1, "Бананы", 180,      PLACE_VOLUME.LARGE,    PLACE_VOLUME.LARGE),

    TOMATO( -1, "Помидоры", 190,    PLACE_VOLUME.LARGE,    PLACE_VOLUME.LARGE),
    CUCUMBER( -1, "Огурцы", 160,    PLACE_VOLUME.LARGE,    PLACE_VOLUME.LARGE),
    POTATO( -1, "Картофель", 60,    PLACE_VOLUME.LARGE,    PLACE_VOLUME.LARGE),
    ONION( -1, "Лук", 90,           PLACE_VOLUME.LARGE,    PLACE_VOLUME.LARGE),
    CARROT( -1, "Морковь", 60,      PLACE_VOLUME.LARGE,    PLACE_VOLUME.LARGE);

    private final int groupIndex;

    private final String title;
    private final int price;
    private final PLACE_VOLUME storePlaceVolume;
    private final PLACE_VOLUME shopPlaceVolume;

    GOOD(
            int groupIndex,
            String title,
            int price,
            PLACE_VOLUME storePlaceVolume,
            PLACE_VOLUME shopPlaceVolume) {
        this.groupIndex = groupIndex;
        this.title = title;
        this.price = price;
        this.storePlaceVolume = storePlaceVolume;
        this.shopPlaceVolume = shopPlaceVolume;
    }

}
