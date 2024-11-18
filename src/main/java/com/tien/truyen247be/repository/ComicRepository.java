package com.tien.truyen247be.repository;

import com.tien.truyen247be.models.Comic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ComicRepository extends JpaRepository<Comic, Long> {
    boolean existsByName(String name);

    int countByName(String name);
}
