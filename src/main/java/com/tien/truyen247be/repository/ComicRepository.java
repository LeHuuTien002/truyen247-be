package com.tien.truyen247be.repository;

import com.tien.truyen247be.models.Comic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComicRepository extends JpaRepository<Comic, Long> {
    boolean existsByName(String name);
    @Query("SELECT c FROM Comic c WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%')) AND c.activate = true")
    List<Comic> findByNameContainingIgnoreCase(String name);
}
