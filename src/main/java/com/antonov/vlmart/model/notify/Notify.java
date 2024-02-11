package com.antonov.vlmart.model.notify;

import com.antonov.vlmart.model.enums.NOTIFY_TYPE;
import lombok.Data;

@Data
public class Notify {
    private NOTIFY_TYPE type;
    private String header;
    private String text;
    private boolean active;
    private int count;

    public Notify(NOTIFY_TYPE type, String header, String text) {
        this.type = type;
        this.header = header;
        this.text = text;
    }

}
