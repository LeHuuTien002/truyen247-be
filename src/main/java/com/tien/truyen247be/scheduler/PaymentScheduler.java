package com.tien.truyen247be.scheduler;

import com.tien.truyen247be.security.services.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Component
public class PaymentScheduler {

    @Autowired
    private PaymentService paymentService;

    @Value("${payment.scheduler.limit:10}") // Số lượng giao dịch mặc định là 10
    private int transactionLimit;

    @Value("${payment.scheduler.validAccountNumbers}") // Các tài khoản hợp lệ
    private String validAccountNumbersConfig;

    @Value("${payment.scheduler.paymentPrefix:SEVQR}") // Tiền tố mã thanh toán
    private String paymentPrefix;

    @Scheduled(cron = "0 */1 * * * *") // Chạy mỗi 5 phút
    public void autoConfirmPayments() {
        try {
            // Xác định khoảng thời gian (mặc định là ngày hiện tại)
            // Ngày bắt đầu: Hôm nay
            String startDate = LocalDate.now().toString();

            // Ngày kết thúc: Ngày mai
            String endDate = LocalDate.now().plusDays(1).toString();

            // Danh sách tài khoản hợp lệ (tách từ cấu hình)
            List<String> validAccountNumbers = Arrays.asList(validAccountNumbersConfig.split(","));

            // Ghi log thông tin lịch chạy
            System.out.println("Bắt đầu autoConfirmPayments từ " + startDate + " đến " + endDate);

            // Gọi phương thức xác nhận thanh toán
            paymentService.confirmPayments(startDate, endDate, transactionLimit, validAccountNumbers, paymentPrefix);

            System.out.println("autoConfirmPayments hoàn tất.");
        } catch (Exception e) {
            // Ghi log lỗi nếu xảy ra vấn đề
            System.err.println("Lỗi khi chạy autoConfirmPayments: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
