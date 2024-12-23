package com.tien.truyen247be.security.services;

import com.tien.truyen247be.Exception.GenreAlreadyExistsException;
import com.tien.truyen247be.mappers.UserMapper;
import com.tien.truyen247be.models.User;
import com.tien.truyen247be.payload.request.UserRequest;
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
import java.util.ArrayList;
import java.util.List;


@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

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
        return ResponseEntity.ok("Cập nhật ảnh đại diện thành công");
    }

    public ResponseEntity<?> getAllUser() {
        List<User> users = userRepository.findAll();
        List<UserResponse> userResponses = new ArrayList<>();
        for (User user : users) {
            UserResponse userResponse = UserMapper.toUserResponse(user);
            userResponses.add(userResponse);
        }
        return ResponseEntity.ok(userResponses);
    }

    public ResponseEntity<?> updateUser(Long userId, UserRequest request) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với id: " + userId));
        user.setActive(request.isActive());
        userRepository.save(user);
        return ResponseEntity.ok("Cập nhật thành công");
    }

    public ResponseEntity<?> deleteUser(Long idUser) {
        if (!userRepository.existsById(idUser)) {
            throw new GenreAlreadyExistsException("Id người dùng không tồn tại!");
        } else {
            userRepository.deleteById(idUser);
        }
        return ResponseEntity.ok("Đã xóa thành công!");
    }
}