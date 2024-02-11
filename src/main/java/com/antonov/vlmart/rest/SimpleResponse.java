package com.antonov.vlmart.rest;

import lombok.Data;

@Data
public class SimpleResponse {
    private boolean success = true;
    private String message;
}
