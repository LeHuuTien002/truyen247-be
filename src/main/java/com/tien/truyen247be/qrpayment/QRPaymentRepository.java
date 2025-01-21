package com.tien.truyen247be.qrpayment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QRPaymentRepository extends JpaRepository<QRPayment, Long> {
    QRPayment findByAmountAfter(double amount);
}
