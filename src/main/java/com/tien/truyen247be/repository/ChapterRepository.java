package com.tien.truyen247be.repository;

import com.tien.truyen247be.models.Chapter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChapterRepository extends JpaRepository<Chapter, Long> {
    boolean existsByTitle(String title);
    boolean existsByComicIdAndChapterNumber(Long comicId, Long chapterNumber);
    Optional<Chapter> findByIdAndComicId(Long chapterId, Long comicId);
}
