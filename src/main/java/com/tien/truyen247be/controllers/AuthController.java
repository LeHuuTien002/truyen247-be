package com.tien.truyen247be.controllers;

import java.util.*;
import java.util.stream.Collectors;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.tien.truyen247be.models.ERole;
import com.tien.truyen247be.models.RegistrationType;
import com.tien.truyen247be.models.Role;
import com.tien.truyen247be.models.User;
import com.tien.truyen247be.payload.request.LoginRequest;
import com.tien.truyen247be.payload.request.OAuth2LoginRequest;
import com.tien.truyen247be.payload.request.SignupRequest;
import com.tien.truyen247be.payload.response.JwtResponse;
import com.tien.truyen247be.payload.response.MessageResponse;
import com.tien.truyen247be.repository.RoleRepository;
import com.tien.truyen247be.repository.UserRepository;
import com.tien.truyen247be.security.jwt.JwtUtils;
import com.tien.truyen247be.security.services.ForgotPasswordService;
import com.tien.truyen247be.security.services.UserDetailsImpl;
import jakarta.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;


@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private static final Logger log = LoggerFactory.getLogger(AuthController.class);
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

    @Autowired
    private ForgotPasswordService forgotPasswordService;

    @Value("${oauth2.client.registration.google.client-id}")
    private String googleClientId;  // Inject client-id từ properties

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        try {
            // Xác thực người dùng bằng AuthenticationManager
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

            // Lấy thông tin người dùng
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

            // Lưu đối tượng Authentication vào SecurityContext
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Tạo JWT token sau khi xác thực thành công
            String jwt = jwtUtils.generateJwtToken(authentication);

            List<String> roles = userDetails.getAuthorities().stream()
                    .map(item -> item.getAuthority())
                    .collect(Collectors.toList());

            // Trả về thành công với JwtResponse
            return ResponseEntity.ok(new JwtResponse(jwt,
                    userDetails.getId(),
                    userDetails.getUsername(),
                    userDetails.getEmail(),
                    roles));

        } catch (DisabledException e) {
            // Trả về thông báo lỗi nếu tài khoản bị vô hiệu hóa
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(Collections.singletonMap("message", "Tài khoản của bạn đã bị khóa hoặc không hoạt động."));
        } catch (BadCredentialsException e) {
            // Trả về thông báo lỗi nếu tài khoản hoặc mật khẩu sai
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Collections.singletonMap("message", "Tài khoản hoặc mật khẩu không chính xác"));
        } catch (Exception e) {
            // Xử lý lỗi không xác định
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("message", "Đã xảy ra lỗi trong quá trình đăng nhập"));
        }
    }

    @PostMapping("/google/signin")
    public ResponseEntity<?> authenticateGoogleUser(@RequestBody OAuth2LoginRequest oAuth2LoginRequest) {
        try {
            // Lấy idToken từ client
            String idToken = oAuth2LoginRequest.getIdToken();

            // Xác thực idToken qua GoogleIdTokenVerifier
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new JacksonFactory())
                    .setAudience(Collections.singletonList(googleClientId))  // Dùng googleClientId từ file properties
                    .build();

            GoogleIdToken googleIdToken = verifier.verify(idToken);
            if (googleIdToken == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Collections.singletonMap("message", "Token Google không hợp lệ"));
            }

            // Lấy thông tin payload từ idToken
            GoogleIdToken.Payload payload = googleIdToken.getPayload();

            String email = payload.getEmail();
            String name = (String) payload.get("name");
            String picture = (String) payload.get("picture");
            String googleId = payload.getSubject(); // Google ID (subject từ token)

            // Kiểm tra xem người dùng đã tồn tại trong database chưa
            Optional<User> userOptional = userRepository.findByEmail(email);
            User user;
            if (userOptional.isPresent()) {
                // Nếu người dùng đã tồn tại, kiểm tra trạng thái tài khoản
                user = userOptional.get();

                if (!user.isActive()) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN)
                            .body(Collections.singletonMap("message", "Tài khoản của bạn đã bị khóa hoặc không hoạt động."));
                }

                // Cập nhật thông tin Google (nếu cần)
                boolean isUpdated = false;
                if (user.getGoogleId() == null || !user.getGoogleId().equals(googleId)) {
                    user.setGoogleId(googleId);
                    isUpdated = true;
                }
                if (user.getPicture() == null || !user.getPicture().equals(picture)) {
                    user.setPicture(picture);
                    isUpdated = true;
                }
                if (isUpdated) {
                    userRepository.save(user);
                }
            } else {
                // Nếu người dùng chưa tồn tại, tạo mới
                user = new User();
                user.setEmail(email);
                user.setUsername(name);
                user.setPassword(UUID.randomUUID().toString()); // Đặt mật khẩu ngẫu nhiên
                user.setPicture(picture);
                user.setRegistrationType(RegistrationType.GOOGLE);
                user.setGoogleId(googleId);
                user.setActive(true); // Kích hoạt tài khoản mới tạo
                user.setRoles(Collections.singleton(roleRepository.findByName(ERole.ROLE_USER)
                        .orElseThrow(() -> new RuntimeException("Role không tồn tại"))));
                userRepository.save(user);
            }

            // Chuyển đổi từ User sang UserDetailsImpl
            UserDetailsImpl userDetails = UserDetailsImpl.build(user);

            // Tạo đối tượng Authentication từ UserDetailsImpl
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Tạo JWT token từ thông tin xác thực
            String jwt = jwtUtils.generateJwtToken(authentication);

            // Trả về JWT và thông tin người dùng
            return ResponseEntity.ok(new JwtResponse(
                    jwt,
                    user.getId(),
                    user.getUsername(),
                    user.getEmail(),
                    user.getRoles().stream()
                            .map(role -> role.getName().name())
                            .collect(Collectors.toList())
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Collections.singletonMap("message", "Xác thực Google không thành công"));
        }
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
        user.setRegistrationType(RegistrationType.STANDARD);
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

    // Endpoint gửi yêu cầu quên mật khẩu
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestParam String email) {
        return forgotPasswordService.sendPasswordResetEmail(email);
    }

    // Endpoint thay đổi mật khẩu
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestParam String token, @RequestParam String newPassword) {
        return forgotPasswordService.resetPassword(token, newPassword);
    }
}