package com.tien.truyen247be.scheduler;

import com.tien.truyen247be.models.User;
import com.tien.truyen247be.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class PremiumStatusScheduler {
    @Autowired
    private UserRepository userRepository;

    @Scheduled(cron = "0 0 0 * * *") // Chạy vào 00:00 mỗi ngày
    public void updatePremiumStatus() {
        // Tìm tất cả người dùng có Premium hết hạn
        List<User> expiredUsers = userRepository.findByPremiumTrueAndPremiumExpiryDateBefore(LocalDate.now());

        for (User user : expiredUsers) {
            user.setPremium(false); // Hết hạn Premium
            userRepository.save(user); // Cập nhật lại database
        }

        System.out.println("Updated premium status for expired users at " + LocalDateTime.now());
    }
}
