package com.tien.truyen247be.security.config;

import com.tien.truyen247be.security.jwt.AuthEntryPointJwt;
import com.tien.truyen247be.security.jwt.AuthTokenFilter;
import com.tien.truyen247be.security.services.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;

import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableMethodSecurity // Kích hoạt bảo mật ở cấp phương thức, cho phép kiểm tra quyền trên các method riêng lẻ
public class WebSecurityConfig {
    @Value("${cloud.aws.credentials.accessKey}")
    private String accessKey;

    @Value("${cloud.aws.credentials.secretKey}")
    private String secretKey;

    @Value("${cloud.aws.region.static}")
    private String region;

    @Autowired
    UserDetailsServiceImpl userDetailsService; // Service để lấy thông tin người dùng từ DB hoặc nguồn khác
    @Autowired
    private AuthEntryPointJwt unauthorizedHandler; // Xử lý khi người dùng không được phép truy cập

    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        // Tạo bean AuthTokenFilter để kiểm tra JWT trong mỗi request
        return new AuthTokenFilter();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        // Liên kết với service cung cấp thông tin người dùng
        authProvider.setUserDetailsService(userDetailsService);
        // Sử dụng BCrypt để mã hóa và giải mã mật khẩu
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        // Cung cấp AuthenticationManager từ cấu hình Authentication
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // Sử dụng BCryptPasswordEncoder để mã hóa mật khẩu
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable()) // Tắt cơ chế bảo vệ CSRF vì hệ thống sử dụng JWT
                .cors(withDefaults())// Kích hoạt CORS với cấu hình mặc định và dùng CorsConfigurationSource
                .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler)) // Xử lý lỗi không có quyền truy cập
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Không dùng session, vì JWT là stateless
                .authorizeHttpRequests(auth ->
                        auth.requestMatchers("/api/auth/**").permitAll() // Cho phép truy cập không cần xác thực với các URL bắt đầu bằng /api/auth/**
                                .requestMatchers("/api/admin/**").hasRole("ADMIN") // Cho phép truy cập không cần xác thực với các URL bắt đầu bằng /api/admin/**
                                .anyRequest().authenticated() // Yêu cầu tất cả các request khác phải xác thực
                );
        // Đăng ký provider để xác thực thông tin người dùng
        http.authenticationProvider(authenticationProvider());
        // Thêm filter kiểm tra JWT trước khi tiến hành xác thực bằng UsernamePasswordAuthenticationFilter
        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build(); // Xây dựng cấu hình bảo mật hoàn chỉnh
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000")); // Chỉ cho phép từ origin này
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS")); // Các phương thức được phép
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type")); // Các headers được phép
        configuration.setAllowCredentials(true); // Cho phép gửi cookies hoặc thông tin xác thực (nếu cần)

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // Áp dụng cấu hình CORS cho tất cả các đường dẫn
        return source;
    }
}
