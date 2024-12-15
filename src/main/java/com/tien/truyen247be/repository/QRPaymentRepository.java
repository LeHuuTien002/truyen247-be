package com.tien.truyen247be.repository;

import com.tien.truyen247be.models.QRPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QRPaymentRepository extends JpaRepository<QRPayment, Long> {
    QRPayment findByAmountAfter(double amount);
}
