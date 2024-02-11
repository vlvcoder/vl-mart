package com.antonov.vlmart.render;

import com.antonov.vlmart.model.enums.GOOD;
import lombok.Data;

@Data
public class GoodWrap {
    private final String name;
    private final String title;
    private final int price;

    public GoodWrap(GOOD good) {
        this.name = good.name();
        this.title = good.title();
        this.price = good.price();
    }
}
