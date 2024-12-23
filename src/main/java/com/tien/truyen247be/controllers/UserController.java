package com.tien.truyen247be.controllers;

import com.tien.truyen247be.payload.request.ChangePasswordRequest;
import com.tien.truyen247be.payload.request.UserRequest;
import com.tien.truyen247be.payload.response.UserResponse;
import com.tien.truyen247be.security.services.ChangePasswordService;
import com.tien.truyen247be.security.services.UserDetailsServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api")
public class UserController {
    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private ChangePasswordService changePasswordService;

    @GetMapping("/admin/users")
    public ResponseEntity<?> getAllUser() {
        return userDetailsService.getAllUser();
    }

    @PutMapping("/admin/users/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody UserRequest request) {
        return userDetailsService.updateUser(id, request);
    }

    @DeleteMapping("/admin/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        return userDetailsService.deleteUser(id);
    }

    @PostMapping("/users/{id}/avatar")
    public ResponseEntity<?> createAvatar(
            @Valid @PathVariable Long id,
            @RequestParam("file") MultipartFile files
    ) throws IOException {
        return userDetailsService.createAvatar(id, files);
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        UserResponse userResponse = userDetailsService.getUserById(id);
        return ResponseEntity.ok(userResponse);
    }

    @PutMapping("/users/change-password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest request) {
        return changePasswordService.changePassword(request);
    }
}
