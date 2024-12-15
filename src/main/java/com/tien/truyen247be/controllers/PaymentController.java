package com.tien.truyen247be.controllers;

import com.tien.truyen247be.models.Payment;
import com.tien.truyen247be.repository.PaymentRepository;
import com.tien.truyen247be.security.services.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class PaymentController {
    @Autowired
    private PaymentService paymentService;

    @Autowired
    private PaymentRepository paymentRepository;

    // Tạo giao dịch thanh toán
    @PostMapping("/payments/create")
    public ResponseEntity<Payment> createPayment(@RequestBody Map<String, Object> request) {
        Long userId = Long.valueOf(request.get("userId").toString());
        String paymentMethod = request.get("paymentMethod").toString();
        String paymentCode = request.get("paymentCode").toString();
        Double amount = Double.valueOf(request.get("amount").toString());

        Payment payment = paymentService.createPayment(userId, paymentCode, paymentMethod, amount);
        return ResponseEntity.ok(payment);
    }

    // Lấy danh sách thanh toán PENDING (Admin)
    @GetMapping("/admin/payments/pending")
    public ResponseEntity<List<Payment>> getPendingPayments() {
        List<Payment> payments = paymentService.getPendingPayments();
        return ResponseEntity.ok(payments);
    }

    // API để lấy danh sách thanh toán
    @GetMapping("/admin/payments")
    public ResponseEntity<List<Payment>> getAllPayments() {
        List<Payment> payments = paymentRepository.findAll();
        return ResponseEntity.ok(payments);
    }
}
