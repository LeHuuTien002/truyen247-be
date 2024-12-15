package com.tien.truyen247be.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "views")
public class View {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "comic_id", referencedColumnName = "id")
    private Comic comic;

    @Column(nullable = false)
    private Long viewsCount;

    @Column(nullable = false)
    private LocalDateTime lastUpdated;
}
