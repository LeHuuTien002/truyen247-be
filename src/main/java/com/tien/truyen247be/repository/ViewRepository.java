package com.tien.truyen247be.repository;

import com.tien.truyen247be.models.View;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ViewRepository extends JpaRepository<View, Long> {
    View findByComicId(Long comicId);
    @Query("select v.viewsCount from View v where v.comic.id = :comicId")
    Long findViewsCountByComicId(Long comicId);
    @Query("SELECT v FROM View v ORDER BY v.viewsCount DESC")
    List<View> findTop10ByOrderByViewsCountDesc(Pageable pageable);

}
