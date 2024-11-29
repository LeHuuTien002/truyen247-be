package com.tien.truyen247be.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

    @ManyToOne(fetch = FetchType.EAGER) // Tải dữ liệu liên quan ngay lập tức
    @JoinColumn(name = "user_id", nullable = false) // Tên cột khóa ngoại
    @JsonIgnore // Bỏ qua trường user khi tuần tự hóa
    private User user; // Quan hệ tới bảng User

    private String paymentMethod; // Ví dụ: BankTransfer
    private String transactionId; // Mã giao dịch ngân hàng
    private String paymentCode; // Mã thanh toán do hệ thống tạo
    private String status; // Ví dụ: PENDING, COMPLETED, FAILED
    private Double amount; // Số tiền cần thanh toán
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
