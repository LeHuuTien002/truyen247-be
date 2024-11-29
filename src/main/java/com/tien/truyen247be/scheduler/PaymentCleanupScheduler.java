package com.tien.truyen247be.scheduler;

import com.tien.truyen247be.models.Payment;
import com.tien.truyen247be.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class PaymentCleanupScheduler {

    @Autowired
    private PaymentRepository paymentRepository;

    @Scheduled(cron = "0 */10 * * * *") // Chạy mỗi 10 phút
    public void cancelUnpaidPayments() {
        LocalDateTime cutoffTime = LocalDateTime.now().minusMinutes(5);
        List<Payment> unpaidPayments = paymentRepository.findByStatusAndCreatedAtBefore("PENDING", cutoffTime);

        if (!unpaidPayments.isEmpty()) {
            unpaidPayments.forEach(payment -> {
                payment.setStatus("CANCELLED");
                payment.setUpdatedAt(LocalDateTime.now());
            });
            paymentRepository.saveAll(unpaidPayments); // Lưu tất cả bản ghi một lần
        }

        System.out.println("Số giao dịch PENDING đã hủy: " + unpaidPayments.size());
    }
}
