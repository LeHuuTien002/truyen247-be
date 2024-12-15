package com.tien.truyen247be.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class QRPayment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String bankName;
    @Column(nullable = false)
    private String cardNumber;
    @Column(nullable = false)
    private String cardName;
    @Column(nullable = false)
    private String QRCode;
    @Column(nullable = false)
    private String paymentContent;
    @Column(nullable = false)
    private Double amount;
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
