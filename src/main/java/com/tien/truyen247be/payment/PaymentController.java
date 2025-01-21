package com.tien.truyen247be.payment;

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

    @PostMapping("/payments/create")
    public ResponseEntity<Payment> createPayment(@RequestBody Map<String, Object> request) {
        Long userId = Long.valueOf(request.get("userId").toString());
        String paymentMethod = request.get("paymentMethod").toString();
        String paymentCode = request.get("paymentCode").toString();
        Double amount = Double.valueOf(request.get("amount").toString());

        Payment payment = paymentService.createPayment(userId, paymentCode, paymentMethod, amount);
        return ResponseEntity.ok(payment);
    }

    @GetMapping("/admin/payments/pending")
    public ResponseEntity<List<Payment>> getPendingPayments() {
        List<Payment> payments = paymentService.getPendingPayments();
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/admin/payments")
    public ResponseEntity<?> getAllPayments() {
        return paymentService.getAllPayments();
    }
}
