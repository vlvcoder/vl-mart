package com.antonov.vlmart.rest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(fluent = true)
public class OpenCloseResponse {
    private boolean isOpened;
}
