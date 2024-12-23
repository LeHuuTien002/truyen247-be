package com.tien.truyen247be.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "genres")
public class Genre {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;
    @Column(name = "create_at", nullable = false, updatable = false)
    private LocalDateTime createAt;
    @Column(name = "update_at")
    private LocalDateTime updateAt;
    @ManyToMany(mappedBy = "genres")
    private Set<Comic> comics = new HashSet<>();

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
