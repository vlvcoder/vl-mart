package com.antonov.vlmart.service.work;

import com.antonov.vlmart.model.enums.STAFF_ROLE;
import com.antonov.vlmart.model.staff.Staff;
import com.antonov.vlmart.render.StaffFolderWrap;
import com.antonov.vlmart.rest.SimpleResponse;

public interface EmployeeService {
    void init();
    Staff manager();

    void loopStaffs();

    String employeeToString();

    Staff createCashier();
    Staff createCashier(String name);
    Staff createShelver();
    Staff createShelver(String name);

    StaffFolderWrap staffs();

    SimpleResponse checkManagerBusy();

    void goWork(String name);

    void fillRaise(int quantity);

    Staff createLoader();

    void cleanBusyStaffs();

    void cashRaise(int quantity);

    int getCashierRestDuration();

    int getShelverRestDuration();

    SimpleResponse decreaseRestDuration(STAFF_ROLE role, int value);
}
