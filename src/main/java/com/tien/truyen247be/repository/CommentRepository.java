package com.tien.truyen247be.repository;

import com.tien.truyen247be.models.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByComicIdAndParentIsNull(Long comicId);

    Long countByComicId(Long comicId);
}
