package com.tien.truyen247be.user;

import com.tien.truyen247be.Exception.dto.ErrorResponse;
import com.tien.truyen247be.Exception.dto.SuccessResponse;
import com.tien.truyen247be.user.dto.ChangePasswordRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ChangePasswordService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public ResponseEntity<?> changePassword(ChangePasswordRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isEmpty()) {
            ErrorResponse response = new ErrorResponse();
            response.setStatus(404);
            response.setMessage("Email không tồn tại!");
            response.setTimestamp(LocalDateTime.now().toString());
            return ResponseEntity.ok(response);
        } else {
            User user = userRepository.findByEmail(request.getEmail()).get();
            if (passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
                user.setPassword(passwordEncoder.encode(request.getNewPassword()));
                userRepository.save(user);

                SuccessResponse response = new SuccessResponse();
                response.setStatus(200);
                response.setMessage("Đổi mật khẩu thành công!");
                response.setTimestamp(LocalDateTime.now().toString());
                return ResponseEntity.ok(response);
            }
            ErrorResponse response = new ErrorResponse();
            response.setStatus(404);
            response.setMessage("Mật khẩu không chính xác!");
            response.setTimestamp(LocalDateTime.now().toString());
            return ResponseEntity.ok(response);
        }
    }
}
