package com.antonov.vlmart.service.notify;

import com.antonov.vlmart.model.enums.NOTIFY_TYPE;
import com.antonov.vlmart.model.notify.Notify;
import com.antonov.vlmart.render.NotifyWrap;
import com.antonov.vlmart.rest.SimpleResponse;

import java.util.List;

public interface NotificationService {
    void send(NOTIFY_TYPE notifyType);
    void sendOnce(NOTIFY_TYPE notifyType);
    void flush(NOTIFY_TYPE notifyType);
    NotifyWrap activeNotify();

    void init();

    void sendAlert(SimpleResponse response);

    SimpleResponse fulshAlert();
}
