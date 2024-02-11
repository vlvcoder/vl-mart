package com.antonov.vlmart.render;

import com.antonov.vlmart.model.enums.STAFF_ROLE;
import lombok.Builder;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

@Data
@Builder
public class StaffCollapsedWrap implements Comparable {
    private STAFF_ROLE role;
    private String roleTitle;
    private String attrColorClass;
    private String attrIconClass;
    private long count;
    private long busyCount;

    @Override
    public int compareTo(@NotNull Object o) {
        if (o instanceof StaffCollapsedWrap) {
            var sc = (StaffCollapsedWrap) o;
            return role.order() - sc.role.order();
        }
        return -1;
    }
}
