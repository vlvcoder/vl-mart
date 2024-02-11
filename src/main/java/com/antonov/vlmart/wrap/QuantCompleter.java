package com.antonov.vlmart.wrap;

import com.antonov.vlmart.model.enums.STAFF_ROLE;
import com.antonov.vlmart.model.staff.Staff;

import java.util.List;

public interface QuantCompleter {
    void quantStart(Staff staff);
    void quantComplete(Staff staff);
    List<Staff> staffs(STAFF_ROLE role);
}
