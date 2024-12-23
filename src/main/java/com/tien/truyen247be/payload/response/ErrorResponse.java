package com.tien.truyen247be.payload.response;

import lombok.Data;

@Data
public class ErrorResponse {
    private int status;
    private String message;
    private String timestamp;

}
