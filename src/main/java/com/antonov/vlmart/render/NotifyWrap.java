package com.antonov.vlmart.render;

import com.antonov.vlmart.model.enums.NOTIFY_STYLE;
import com.antonov.vlmart.model.notify.Notify;
import lombok.Data;

@Data
public class NotifyWrap {
    private final String name;
    private final String header;
    private final NOTIFY_STYLE style;
    private final String text;

    public NotifyWrap(Notify notify) {
        this.name = notify.getType().name();
        this.header = notify.getHeader();
        this.style = notify.getType().getStyle();
        this.text = notify.getText();
    }
}
