package com.antonov.vlmart.render;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BusinessWrap {
    private long moneyCount;
    private String money;
    private String like;
    private String dislike;
}
