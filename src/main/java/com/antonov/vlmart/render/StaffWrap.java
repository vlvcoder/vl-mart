package com.antonov.vlmart.render;

import com.antonov.vlmart.model.enums.STAFF_ROLE;
import com.antonov.vlmart.model.staff.Staff;
import lombok.Data;

@Data
public class StaffWrap {
    private String name;
    private STAFF_ROLE role;
    private long restFinishMillis;
    private volatile boolean isBusy;

    private String attrColorClass;
    private String attrIconClass;

    public StaffWrap(Staff staff) {
        this.name = staff.name();
        this.role = staff.role();
        this.restFinishMillis = staff.restFinishMillis();
        this.isBusy = staff.isBusy();

        switch (staff.role()) {
            case MANAGER:
                attrColorClass = "bg-success";
                attrIconClass = "fa-tasks";
                break;
            case CASHIER:
                attrColorClass = "bg-warning";
                attrIconClass = "fa-calculator";
                break;
            case SHELVER:
                attrColorClass = "bg-primary";
                attrIconClass = "fa-shopping-cart";
                break;
            case LOADER:
                attrColorClass = "bg-danger";
                attrIconClass = "fa-truck";
                break;
        }

    }
}
