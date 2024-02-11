package com.antonov.vlmart.model.staff;

import com.antonov.vlmart.model.enums.STAFF_ROLE;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(fluent = true)
public class Staff {
    private String name;
    private STAFF_ROLE role;
    private long restFinishMillis;
    private volatile boolean isBusy;

    @Override
    public String toString() {
        return name + " [" + role + "]" + (isBusy ? " - Выполнение задания" : "");
    }

}
