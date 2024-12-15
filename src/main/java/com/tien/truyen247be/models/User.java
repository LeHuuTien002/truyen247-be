package com.tien.truyen247be.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "users", uniqueConstraints = {@UniqueConstraint(columnNames = "username"), @UniqueConstraint(columnNames = "email")})
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 20)
    private String username;

    @NotBlank
    @Size(max = 50)
    @Email
    private String email;

    @NotBlank
    @Size(max = 120)
    private String password;

    private String resetToken;

    private String googleId;

    private String picture;

    private boolean active = true;

    private boolean premium = false;

    private LocalDate premiumExpiryDate;

    @Enumerated
    private RegistrationType registrationType;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore // Bỏ qua danh sách payments khi tuần tự hóa JSON
    private List<Payment> payments = new ArrayList<>(); // Danh sách các thanh toán liên quan đến người dùng

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    @Column(name = "create_at", nullable = false, updatable = false)
    private LocalDateTime createAt;

    @Column(name = "update_at")
    private LocalDateTime updateAt;

    private LocalDateTime lastPaymentCreateAt;

    // Phương thức này sẽ chạy trước khi một bản ghi mới được lưu vào cơ sở dữ liệu
    @PrePersist
    protected void createAt() {
        this.createAt = LocalDateTime.now(); // Lấy thời gian hiện tại khi tạo mới bản ghi
    }

    // Phương thức này sẽ chạy trước mỗi lần cập nhật bản ghi
    @PreUpdate
    protected void updateAt() {
        this.updateAt = LocalDateTime.now(); // Cập nhật thời gian mỗi khi bản ghi được cập nhật
    }
}
