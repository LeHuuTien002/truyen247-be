package com.tien.truyen247be.chapter;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChapterRepository extends JpaRepository<Chapter, Long> {
    boolean existsByTitle(String title);
    boolean existsByChapterNumber(Long chapterNumber);
    List<Chapter> findByComicId(Long comicId);
    boolean existsByComicIdAndChapterNumber(Long comicId, Long chapterNumber);
    Optional<Chapter> findByIdAndComicId(Long chapterId, Long comicId);
}
