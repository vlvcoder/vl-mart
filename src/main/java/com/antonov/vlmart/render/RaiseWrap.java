package com.antonov.vlmart.render;

import lombok.Data;

@Data
public class RaiseWrap {
    private final int quantity;
    private final String title;
    private final String price;

    public RaiseWrap(int quantity, String title, String price) {
        this.quantity = quantity;
        this.title = title;
        this.price = price;
    }
}
