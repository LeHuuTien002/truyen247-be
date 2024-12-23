package com.tien.truyen247be.payload.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class QRPaymentResponse {
    private Long id;
    private String bankName;
    private String QRCode;
    private String paymentContent;
    private String cardNumber;
    private String cardName;
    private Double amount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
