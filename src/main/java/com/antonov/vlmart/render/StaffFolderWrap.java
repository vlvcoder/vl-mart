package com.antonov.vlmart.render;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class StaffFolderWrap {
    private List<StaffWrap> staffs;
    private List<StaffCollapsedWrap> staffsCollapsed;
}
