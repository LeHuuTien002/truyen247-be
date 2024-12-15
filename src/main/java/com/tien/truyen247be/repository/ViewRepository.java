package com.tien.truyen247be.repository;

import com.tien.truyen247be.models.View;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ViewRepository extends JpaRepository<View, Long> {
    View findByComicId(Long comicId);
}
