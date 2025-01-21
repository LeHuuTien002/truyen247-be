package com.tien.truyen247be.payment;

import com.fasterxml.jackson.databind.JsonNode;
import com.tien.truyen247be.user.User;
import com.tien.truyen247be.payment.dto.PaymentResponse;
import com.tien.truyen247be.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class PaymentService {
    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VietinBankApiService vietinBankApiService;

    @Value("${payment.scheduler.limit:10}")
    private int transactionLimit;

    @Value("${payment.scheduler.validAccountNumbers}")
    private String validAccountNumbersConfig;

    @Value("${payment.scheduler.paymentPrefix:SEVQR}")
    private String paymentPrefix;


    public Payment createPayment(Long userId, String paymentCode, String paymentMethod, Double amount) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String startDate = LocalDate.now().toString();
        String endDate = LocalDate.now().plusDays(1).toString();
        List<String> validAccountNumbers = Arrays.asList(validAccountNumbersConfig.split(","));

        Payment existingPayment = paymentRepository.findByPaymentCode(paymentCode);
        if (existingPayment != null) {
            confirmPayments(startDate, endDate, transactionLimit, validAccountNumbers, paymentPrefix);
            return existingPayment;
        }

        Payment payment = new Payment();
        payment.setUser(user);
        payment.setPaymentMethod(paymentMethod);
        payment.setPaymentCode(paymentCode);
        payment.setAmount(amount);
        payment.setStatus("PENDING");
        payment.setCreatedAt(LocalDateTime.now());
        payment.setUpdatedAt(LocalDateTime.now());

        user.setLastPaymentCreateAt(LocalDateTime.now());
        userRepository.save(user);

        Payment paymentSaved = paymentRepository.save(payment);
        confirmPayments(startDate, endDate, transactionLimit, validAccountNumbers, paymentPrefix);

        return paymentSaved;
    }

    public ResponseEntity<List<PaymentResponse>> getAllPayments() {
        List<Payment> payments = paymentRepository.findAll();
        List<PaymentResponse> paymentResponses = new ArrayList<>();
        for (Payment payment : payments) {
            PaymentResponse paymentResponse = new PaymentResponse();
            paymentResponse.setPaymentCode(payment.getPaymentCode());
            paymentResponse.setAmount(payment.getAmount());
            paymentResponse.setId(payment.getId());

            User user = userRepository.findUserById(payment.getUser().getId())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            paymentResponse.setEmail(user.getEmail());
            paymentResponse.setCreatedAt(payment.getCreatedAt());
            paymentResponse.setUpdatedAt(payment.getUpdatedAt());
            paymentResponse.setPaymentCode(payment.getPaymentCode());
            paymentResponse.setPaymentMethod(payment.getPaymentMethod());
            paymentResponse.setStatus(payment.getStatus());
            paymentResponses.add(paymentResponse);
        }
        return ResponseEntity.ok(paymentResponses);
    }

    public void confirmPayments(String startDate, String endDate, int limit, List<String> validAccountNumbers, String paymentPrefix) {
        try {
            JsonNode response = vietinBankApiService.getTransactions(startDate, endDate, limit);
            JsonNode transactions = response.get("transactions");

            if (transactions == null || !transactions.isArray() || transactions.size() == 0) {
                System.out.println("Không có giao dịch nào được trả về từ API trong khoảng thời gian đã cho.");
                return;
            }

            for (JsonNode transaction : transactions) {
                try {
                    String transactionId = transaction.get("id").asText();
                    String accountNumber = transaction.get("account_number").asText();
                    String transactionContent = transaction.get("transaction_content").asText();
                    Double amountIn = transaction.get("amount_in").asDouble();

                    if (!validAccountNumbers.contains(accountNumber)) {
                        System.out.println("Giao dịch không thuộc tài khoản hợp lệ: " + accountNumber);
                        continue;
                    }

                    String paymentCode = extractPaymentCode(transactionContent, paymentPrefix);
                    if (paymentCode == null) {
                        System.out.println("Không tìm thấy mã thanh toán trong nội dung giao dịch: " + transactionContent);
                        continue;
                    }

                    Payment payment = paymentRepository.findByPaymentCode(paymentCode);
                    if (payment == null) {
                        System.out.println("Không tìm thấy thanh toán với mã: " + paymentCode);
                        continue;
                    }

                    if (!"PENDING".equals(payment.getStatus())) {
                        System.out.println("Giao dịch không ở trạng thái PENDING, bỏ qua: " + paymentCode);
                        continue;
                    }

                    if (!payment.getAmount().equals(amountIn)) {
                        System.out.println("Số tiền không khớp cho mã thanh toán: " + paymentCode);
                        continue;
                    }

                    payment.setTransactionId(transactionId);
                    payment.setStatus("COMPLETED");
                    payment.setUpdatedAt(LocalDateTime.now());

                    User user = payment.getUser();
                    LocalDate premiumExpiryDate = null;

                    if (amountIn == 20000) {
                        premiumExpiryDate = LocalDate.now().plusMonths(1);
                    } else if (amountIn == 200000) {
                        premiumExpiryDate = LocalDate.now().plusYears(1);
                    } else {
                        System.out.println("Số tiền không hợp lệ cho giao dịch: " + paymentCode);
                        continue;
                    }

                    user.setPremium(true);
                    user.setPremiumExpiryDate(premiumExpiryDate);

                    userRepository.save(user);
                    paymentRepository.save(payment);

                    System.out.println("Thanh toán đã được xác nhận: " + paymentCode);
                } catch (Exception e) {
                    System.out.println("Lỗi khi xử lý giao dịch: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Lỗi khi xác minh giao dịch: " + e.getMessage());
        }
    }


    public List<Payment> getPendingPayments() {
        return paymentRepository.findByStatus("PENDING");
    }

    private String extractPaymentCode(String transactionContent, String paymentPrefix) {
        if (!transactionContent.contains(paymentPrefix)) {
            return null;
        }

        String[] contentParts = transactionContent.split(paymentPrefix, 2);
        if (contentParts.length < 2) {
            return null;
        }

        return paymentPrefix + contentParts[1].trim();
    }

}
