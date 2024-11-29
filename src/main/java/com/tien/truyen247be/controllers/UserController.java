package com.tien.truyen247be.controllers;

import com.tien.truyen247be.payload.request.UserRequest;
import com.tien.truyen247be.security.services.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
public class UserController {
    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @GetMapping("/users")
    public ResponseEntity<?> getAllUser() {
        return userDetailsService.getAllUser();
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody UserRequest request) {
        return userDetailsService.updateUser(id, request);
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        return userDetailsService.deleteUser(id);
    }
}
