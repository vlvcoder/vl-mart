package com.antonov.vlmart.model.area;

import com.antonov.vlmart.model.staff.Staff;
import com.antonov.vlmart.utils.Utils;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Data
@Accessors(fluent = true)
public class Employee implements Area {
    private List<Staff> staffs = Collections.synchronizedList(new ArrayList<>());

    private int cashierRestDuration;
    private int shelverRestDuration;

    @Override
    public String statusText() {
        var builder = statusHeader("#122fff");
        Utils.statusStaffList(staffs, builder);
        return builder.line().toString();
    }

    @Override
    public String toString() {
        return "Отдел кадров";
    }
}
