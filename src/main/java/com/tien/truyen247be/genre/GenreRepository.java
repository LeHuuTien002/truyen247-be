package com.tien.truyen247be.genre;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GenreRepository extends JpaRepository<Genre, Long> {
    Boolean existsByName(String name);

    Genre findByName(String name);
}
