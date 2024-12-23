package com.tien.truyen247be.models;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "history", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "comic_id"})
})
public class History {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    @ManyToOne
    @JoinColumn(name = "comic_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Comic comic;

    @ManyToOne
    @JoinColumn(name = "chapter_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Chapter chapter;

    @Column(nullable = false)
    private LocalDateTime lastReadTime;
}
