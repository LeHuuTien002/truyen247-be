package com.tien.truyen247be.security.services;

import com.tien.truyen247be.models.User;
import com.tien.truyen247be.payload.response.ErrorResponse;
import com.tien.truyen247be.payload.response.SuccessResponse;
import com.tien.truyen247be.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class ForgotPasswordService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public ResponseEntity<?> sendPasswordResetEmail(String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            String token = UUID.randomUUID().toString();
            user.setResetToken(token);
            userRepository.save(user);

            String resetLink = "http://localhost:3000/reset-password?token=" + token;
            sendEmail(email, resetLink);

            SuccessResponse response = new SuccessResponse();
            response.setStatus(200);
            response.setMessage("Yêu cầu đã được gửi đến email của bạn.");
            response.setTimestamp(LocalDateTime.now().toString());
            return ResponseEntity.ok(response);
        }
        ErrorResponse response = new ErrorResponse();
        response.setStatus(404);
        response.setMessage("Email không tồn tại");
        response.setTimestamp(LocalDateTime.now().toString());
        return ResponseEntity.ok(response);
    }

    private void sendEmail(String to, String resetLink) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Đặt lại mật khẩu của bạn");
        message.setText("Nhấp vào liên kết để đặt lại mật khẩu của bạn: " + resetLink);
        mailSender.send(message);
    }

    public ResponseEntity<?> resetPassword(String token, String newPassword) {
        User user = userRepository.findByResetToken(token);
        if (user == null) {
            ErrorResponse response = new ErrorResponse();
            response.setStatus(404);
            response.setMessage("Token không hợp lệ");
            response.setTimestamp(LocalDateTime.now().toString());
            return ResponseEntity.ok(response);
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetToken(null);
        userRepository.save(user);

        SuccessResponse response = new SuccessResponse();
        response.setStatus(200);
        response.setMessage("Đặt lại mật khẩu thành công");
        response.setTimestamp(LocalDateTime.now().toString());
        return ResponseEntity.ok(response);
    }
}
