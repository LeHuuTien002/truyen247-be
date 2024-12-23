package com.tien.truyen247be.security.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.tien.truyen247be.models.Payment;
import com.tien.truyen247be.models.User;
import com.tien.truyen247be.repository.PaymentRepository;
import com.tien.truyen247be.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
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

    @Value("${payment.scheduler.limit:10}") // Số lượng giao dịch mặc định là 10
    private int transactionLimit;

    @Value("${payment.scheduler.validAccountNumbers}") // Các tài khoản hợp lệ
    private String validAccountNumbersConfig;

    @Value("${payment.scheduler.paymentPrefix:SEVQR}") // Tiền tố mã thanh toán
    private String paymentPrefix;


    // Tạo giao dịch thanh toán
    public Payment createPayment(Long userId, String paymentCode, String paymentMethod, Double amount) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String startDate = LocalDate.now().toString();
        String endDate = LocalDate.now().plusDays(1).toString();
        List<String> validAccountNumbers = Arrays.asList(validAccountNumbersConfig.split(","));

        // Kiểm tra xem giao dịch đã tồn tại chưa
        Payment existingPayment = paymentRepository.findByPaymentCode(paymentCode);
        if (existingPayment != null) {
            // Nếu giao dịch đã tồn tại, xác nhận lại giao dịch
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

    // Xác minh giao dịch và cập nhật thanh toán
    public void confirmPayments(String startDate, String endDate, int limit, List<String> validAccountNumbers, String paymentPrefix) {
        try {
            // Lấy danh sách giao dịch từ API SePay
            JsonNode response = vietinBankApiService.getTransactions(startDate, endDate, limit);
            JsonNode transactions = response.get("transactions");

            // Kiểm tra danh sách giao dịch
            if (transactions == null || !transactions.isArray() || transactions.size() == 0) {
                System.out.println("Không có giao dịch nào được trả về từ API trong khoảng thời gian đã cho.");
                return; // Dừng lại nếu không có giao dịch
            }

            // Duyệt qua danh sách giao dịch
            for (JsonNode transaction : transactions) {
                try {
                    // Lấy thông tin giao dịch
                    String transactionId = transaction.get("id").asText(); // ID giao dịch từ API
                    String accountNumber = transaction.get("account_number").asText(); // Số tài khoản
                    String transactionContent = transaction.get("transaction_content").asText(); // Nội dung giao dịch
                    Double amountIn = transaction.get("amount_in").asDouble(); // Số tiền vào

                    // Kiểm tra tài khoản có hợp lệ không
                    if (!validAccountNumbers.contains(accountNumber)) {
                        System.out.println("Giao dịch không thuộc tài khoản hợp lệ: " + accountNumber);
                        continue;
                    }

                    // Tách mã thanh toán từ nội dung giao dịch
                    String paymentCode = extractPaymentCode(transactionContent, paymentPrefix);
                    if (paymentCode == null) {
                        System.out.println("Không tìm thấy mã thanh toán trong nội dung giao dịch: " + transactionContent);
                        continue;
                    }

                    // Tìm giao dịch trong hệ thống dựa trên mã thanh toán
                    Payment payment = paymentRepository.findByPaymentCode(paymentCode);
                    if (payment == null) {
                        System.out.println("Không tìm thấy thanh toán với mã: " + paymentCode);
                        continue;
                    }

                    // Chỉ xử lý các giao dịch có trạng thái PENDING
                    if (!"PENDING".equals(payment.getStatus())) {
                        System.out.println("Giao dịch không ở trạng thái PENDING, bỏ qua: " + paymentCode);
                        continue;
                    }

                    // So sánh số tiền
                    if (!payment.getAmount().equals(amountIn)) {
                        System.out.println("Số tiền không khớp cho mã thanh toán: " + paymentCode);
                        continue;
                    }

                    // Cập nhật trạng thái thanh toán
                    payment.setTransactionId(transactionId);
                    payment.setStatus("COMPLETED");
                    payment.setUpdatedAt(LocalDateTime.now());

                    // Xử lý thời hạn Premium
                    User user = payment.getUser();
                    LocalDate premiumExpiryDate = LocalDate.now().plusMonths(1); // Gói 1 tháng
                    user.setPremium(true);
                    user.setPremiumExpiryDate(premiumExpiryDate);

                    // Lưu cập nhật vào database
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

    // Lấy danh sách thanh toán PENDING
    public List<Payment> getPendingPayments() {
        return paymentRepository.findByStatus("PENDING");
    }

    // Hàm tách mã thanh toán từ nội dung giao dịch
    private String extractPaymentCode(String transactionContent, String paymentPrefix) {
        // Kiểm tra nếu nội dung chứa tiền tố (prefix)
        if (!transactionContent.contains(paymentPrefix)) {
            return null; // Nếu không chứa tiền tố, trả về null
        }

        // Tách phần sau tiền tố
        String[] contentParts = transactionContent.split(paymentPrefix, 2); // Chia chỉ thành 2 phần
        if (contentParts.length < 2) {
            return null; // Nếu không có phần nào sau tiền tố, trả về null
        }

        // Trả về mã thanh toán đầy đủ (prefix + phần sau prefix đã loại bỏ khoảng trắng thừa)
        return paymentPrefix + contentParts[1].trim();
    }

}
