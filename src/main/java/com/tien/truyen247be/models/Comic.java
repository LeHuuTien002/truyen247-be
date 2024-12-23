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
@Table(name = "comics")
public class Comic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String otherName;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private String author;

    @Column(nullable = false)
    private boolean activate; // Mặc định truyện đang hoạt động

    @Column(nullable = false)
    private String thumbnail; // URL hoặc đường dẫn tới ảnh bìa

    @Column(name = "create_at", nullable = false, updatable = false)
    private LocalDateTime createAt;

    @Column(name = "update_at")
    private LocalDateTime updateAt;

    @OneToOne(mappedBy = "comic", cascade = CascadeType.ALL, orphanRemoval = true)
    private View view;

    @OneToMany(mappedBy = "comic", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Chapter> chapters = new HashSet<>();

    @ManyToMany
    @JoinTable(name = "comic_genre",
            joinColumns = @JoinColumn(name = "comic_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id")
    )
    private Set<Genre> genres = new HashSet<>();


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
