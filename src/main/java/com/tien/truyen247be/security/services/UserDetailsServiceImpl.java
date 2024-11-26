package com.tien.truyen247be.security.services;

import com.tien.truyen247be.mappers.UserMapper;
import com.tien.truyen247be.models.User;
import com.tien.truyen247be.payload.response.UserResponse;
import com.tien.truyen247be.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;


@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    UserRepository userRepository;
    @Autowired
    private S3Service s3Service;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy người dùng có email: " + email));

        return UserDetailsImpl.build(user);
    }

    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        return UserMapper.toUserResponse(user);
    }

    public ResponseEntity<?> createAvatar(Long userId, MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File tải lên không được để trống.");
        }
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với id " + userId));
        String avatar = s3Service.uploadFile(file);
        user.setPicture(avatar);
        userRepository.save(user);
        return ResponseEntity.ok("Cập nhật avatar thành công!");
    }
}