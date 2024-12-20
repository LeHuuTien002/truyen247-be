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
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.ClientRegistrations;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
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
    // Inject các giá trị từ application.properties
    @Value("${oauth2.client.registration.google.client-id}")
    private String googleClientId;

    @Value("${oauth2.client.registration.google.client-secret}")
    private String googleClientSecret;

    @Value("${oauth2.client.registration.google.scope}")
    private String googleScope;

    @Value("${oauth2.client.registration.google.redirect-uri}")
    private String googleRedirectUri;

    @Autowired
    private UserDetailsServiceImpl userDetailsService; // Service để lấy thông tin người dùng từ DB hoặc nguồn khác

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
        http.csrf(csrf -> csrf.disable())
                .cors(withDefaults())
                .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/public/**").permitAll()
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                ).oauth2Login(oauth2 ->
                        oauth2
                                .loginPage("/login")  // Đặt URL login cho OAuth2
                                .failureUrl("/api/auth/failure")  // URL khi login thất bại
                );
        http.authenticationProvider(authenticationProvider());
        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public ClientRegistrationRepository clientRegistrationRepository() {
        return new InMemoryClientRegistrationRepository(googleClientRegistration());
    }

    // OAuth2AuthorizedClientRepository không cần khai báo riêng, Spring sẽ tự động cấu hình.
    // Đoạn mã này chỉ cần clientRegistrationRepository
    private ClientRegistration googleClientRegistration() {
        return ClientRegistrations
                .fromIssuerLocation("https://accounts.google.com")
                .clientId(googleClientId)
                .clientSecret(googleClientSecret)
                .scope(googleScope)
                .redirectUri(googleRedirectUri)
                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000")); // Chỉ cho phép từ origin này
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS")); // Các phương thức được phép
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type")); // Các headers được phép
        // Thêm các header để giải quyết vấn đề Cross-Origin-Opener-Policy
        configuration.addExposedHeader("Cross-Origin-Opener-Policy");
        configuration.addExposedHeader("Cross-Origin-Embedder-Policy");
        configuration.setAllowCredentials(true); // Cho phép gửi cookies hoặc thông tin xác thực (nếu cần)

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // Áp dụng cấu hình CORS cho tất cả các đường dẫn
        return source;
    }
}
