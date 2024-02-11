package com.antonov.vlmart.wrap;

import com.antonov.vlmart.model.enums.ESTIMATION_TYPE;
import com.antonov.vlmart.render.BusinessWrap;

public interface Liker {
    void like(ESTIMATION_TYPE estimationType);
    String statusText();
}
