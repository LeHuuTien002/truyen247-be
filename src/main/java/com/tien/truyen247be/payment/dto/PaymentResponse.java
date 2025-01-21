package com.tien.truyen247be.payment.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
@Data
@Getter
@Setter
public class PaymentResponse {
        private Long id;
        private String email;
        private String paymentMethod;
        private String transactionId;
        private String paymentCode;
        private String status;
        private Double amount;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
}
