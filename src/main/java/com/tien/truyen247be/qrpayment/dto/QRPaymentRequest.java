package com.tien.truyen247be.qrpayment.dto;

import lombok.Data;

@Data
public class QRPaymentRequest {
    private String bankName;
    private String cardNumber;
    private String cardName;
    private String QRCode;
    private String paymentContent;
    private Double amount;
}
