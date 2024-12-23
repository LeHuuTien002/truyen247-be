package com.tien.truyen247be.payload.request;

import lombok.Data;

@Data
public class PaymentRequest {
    private boolean premium;
    private String duration; // "1M" for 1 month, "1Y" for 1 year
}
