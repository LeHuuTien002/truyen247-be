package com.tien.truyen247be.repository;

import com.tien.truyen247be.models.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PageRepository extends JpaRepository<Page, Long> {
    boolean existsByPageNumber(Long pageNumber);

    @Query("SELECT COUNT(p) > 0 FROM Page p WHERE p.pageNumber = :pageNumber AND p.chapter.id = :chapterId")
    boolean existsByPageNumberAndChapterId(@Param("pageNumber") Long pageNumber, @Param("chapterId") Long chapterId);

    Optional<Page> findByIdAndChapterId(Long pageId, Long chapterId);
}
