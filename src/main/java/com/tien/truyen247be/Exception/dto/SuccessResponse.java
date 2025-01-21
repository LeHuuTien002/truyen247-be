package com.tien.truyen247be.Exception.dto;

import lombok.Data;

@Data
public class SuccessResponse {
    private int status;
    private String message;
    private String timestamp;
}
