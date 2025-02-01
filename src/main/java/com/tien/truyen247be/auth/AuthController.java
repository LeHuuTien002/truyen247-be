package com.tien.truyen247be.auth;

import com.tien.truyen247be.user.*;
import com.tien.truyen247be.auth.payload.LoginRequest;
import com.tien.truyen247be.auth.payload.OAuth2LoginRequest;
import com.tien.truyen247be.auth.payload.SignupRequest;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.security.auth.login.LoginException;


@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private ForgotPasswordService forgotPasswordService;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) throws LoginException {
        return authService.signIn(loginRequest);
    }

    @PostMapping("/google/signin")
    public ResponseEntity<?> authenticateGoogleUser(@RequestBody OAuth2LoginRequest oAuth2LoginRequest) {
        return authService.googleSignIn(oAuth2LoginRequest);
    }


    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        return authService.signUp(signUpRequest);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestParam String email) {
        return forgotPasswordService.sendPasswordResetEmail(email);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestParam String token, @RequestParam String newPassword) {
        return forgotPasswordService.resetPassword(token, newPassword);
    }
}