package com.tien.truyen247be.controllers;

import java.util.*;
import java.util.stream.Collectors;

import com.tien.truyen247be.models.ERole;
import com.tien.truyen247be.models.Role;
import com.tien.truyen247be.models.User;
import com.tien.truyen247be.payload.request.LoginRequest;
import com.tien.truyen247be.payload.request.SignupRequest;
import com.tien.truyen247be.payload.response.JwtResponse;
import com.tien.truyen247be.payload.response.MessageResponse;
import com.tien.truyen247be.repository.RoleRepository;
import com.tien.truyen247be.repository.UserRepository;
import com.tien.truyen247be.security.jwt.JwtUtils;
import com.tien.truyen247be.security.services.UserDetailsImpl;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.*;


@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        try {
            // Xác thực người dùng bằng AuthenticationManager
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

            // Lưu đối tượng Authentication vào SecurityContext
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Tạo JWT token sau khi xác thực thành công
            String jwt = jwtUtils.generateJwtToken(authentication);

            // Lấy thông tin người dùng
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            List<String> roles = userDetails.getAuthorities().stream()
                    .map(item -> item.getAuthority())
                    .collect(Collectors.toList());

            // Trả về thành công với JwtResponse
            return ResponseEntity.ok(new JwtResponse(jwt,
                    userDetails.getId(),
                    userDetails.getUsername(),
                    userDetails.getEmail(),
                    roles));

        } catch (BadCredentialsException e) {
            // Trả về thông báo lỗi cụ thể khi đăng nhập sai
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Collections.singletonMap("message", "Tài khoản hoặc mật khẩu không chính xác"));
        }
    }


    @GetMapping("/api/auth/google")
    public ResponseEntity<?> googleLoginSuccess(OAuth2AuthenticationToken authentication) {
        // Lấy thông tin người dùng từ Google
        String email = authentication.getPrincipal().getAttributes().get("email").toString();
        String name = authentication.getPrincipal().getAttributes().get("name").toString();
        String googleId = authentication.getPrincipal().getName(); // Lấy Google ID từ OAuth token
        String picture = authentication.getPrincipal().getAttributes().get("picture").toString();

        // Kiểm tra người dùng đã tồn tại chưa
        Optional<User> existingUser = userRepository.findByEmail(email);

        User user;
        if (existingUser.isEmpty()) {
            // Tạo tài khoản mới cho người dùng
            user = new User();
            user.setUsername(name);
            user.setEmail(email);
            user.setGoogle_id(googleId);
            user.setPicture(picture);

            Set<Role> roles = new HashSet<>();
            Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);

            user.setRoles(roles);
            userRepository.save(user); // Lưu thông tin người dùng mới vào DB
        } else {
            // Nếu người dùng đã tồn tại, lấy thông tin người dùng
            user = existingUser.get();
        }

        // Tạo đối tượng Authentication từ thông tin người dùng
        Authentication authenticationToken = new UsernamePasswordAuthenticationToken(user.getEmail(), null, user.getAuthorities());

        // Đặt Authentication vào SecurityContext
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        // Tạo JWT token dựa trên thông tin xác thực
        String jwt = jwtUtils.generateJwtToken(authenticationToken);

        // Lấy thông tin chi tiết của người dùng từ đối tượng Authentication
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        // Lấy danh sách các vai trò (roles) của người dùng từ authorities
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        // Trả về JWT token cùng với thông tin người dùng
        return ResponseEntity.ok(new JwtResponse(jwt,
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                roles));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Tên người dùng đã được sử dụng!"));
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Email đã được sử dụng!"));
        }

        // Create new user's account
        User user = new User();
        user.setUsername(signUpRequest.getUsername());
        user.setEmail(signUpRequest.getEmail());
        user.setPassword(encoder.encode(signUpRequest.getPassword()));

        Set<String> strRoles = signUpRequest.getRole();
        Set<Role> roles = new HashSet<>();

        if (strRoles == null) {
            Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy vai trò."));
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                if (role.equals("admin")) {
                    Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                            .orElseThrow(() -> new RuntimeException("Không tìm thấy vai trò.."));
                    roles.add(adminRole);
                } else {
                    Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                            .orElseThrow(() -> new RuntimeException("Không tìm thấy vai trò.."));
                    roles.add(userRole);
                }
            });
        }

        user.setRoles(roles);
        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("Đã đăng ký thành công!"));
    }
}