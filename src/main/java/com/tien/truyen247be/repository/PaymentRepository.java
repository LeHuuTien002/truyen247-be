package com.tien.truyen247be.repository;

import com.tien.truyen247be.models.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByUserId(Long userId); // Lấy thanh toán theo User ID

    List<Payment> findByStatus(String status);

    List<Payment> findByStatusAndCreatedAtBefore(String status, LocalDateTime cutoffTime);

    Payment findByPaymentCode(String paymentCode);
}
