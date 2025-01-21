package com.tien.truyen247be.payment;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tien.truyen247be.user.User;
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
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    private String paymentMethod;
    private String transactionId;
    private String paymentCode;
    private String status;
    private Double amount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
