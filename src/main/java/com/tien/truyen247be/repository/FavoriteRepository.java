package com.tien.truyen247be.repository;

import com.tien.truyen247be.models.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    List<Favorite> findByUserId(Long userId);

    Long countByComicId(Long comicId);

    Optional<Favorite> findByUserIdAndComicId(Long userId, Long comicId);
}
