package com.tien.truyen247be.history;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

@Repository
public interface HistoryRepository extends JpaRepository<History, Long> {
    Optional<History> findByUserIdAndComicId(Long userId, Long comicId);

    boolean existsByUserIdAndComicId(Long userId, Long comicId);

    List<History> findByUserId(Long userId);

    List<History> findByUserIdOrderByLastReadTimeDesc(Long userId, Pageable pageable);
}
