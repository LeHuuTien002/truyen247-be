package com.tien.truyen247be.comment;

import com.tien.truyen247be.exception.ResourceNotFoundException;
import com.tien.truyen247be.comic.Comic;
import com.tien.truyen247be.user.User;
import com.tien.truyen247be.comment.dto.CommentRequest;
import com.tien.truyen247be.comment.dto.ReplyCommentRequest;
import com.tien.truyen247be.comment.dto.CommentResponse;
import com.tien.truyen247be.user.dto.UserResponse;
import com.tien.truyen247be.comic.ComicRepository;
import com.tien.truyen247be.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ComicRepository comicRepository;

    public ResponseEntity<?> addComment(CommentRequest commentRequest) {
        Comic comic = comicRepository.findById(commentRequest.getComicId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy truyện tranh"));
        User user = userRepository.findById(commentRequest.getUserId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        Comment comment = new Comment();
        comment.setContent(commentRequest.getContent());
        comment.setUser(user);
        comment.setComic(comic);

        commentRepository.save(comment);

        return ResponseEntity.ok("Đã thêm bình luận");
    }

    public ResponseEntity<List<CommentResponse>> getComments(Long comicId) {
        List<Comment> comments = commentRepository.findByComicIdAndParentIsNull(comicId);

        List<CommentResponse> commentResponses = comments.stream()
                .map(this::mapToCommentResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(commentResponses);
    }

    private CommentResponse mapToCommentResponse(Comment comment) {
        return new CommentResponse(
                comment.getId(),
                comment.getContent(),
                new UserResponse(comment.getUser().getId(), comment.getUser().getUsername(), comment.getUser().getPicture()),
                comment.getReplies().stream()
                        .map(this::mapToCommentResponse)
                        .collect(Collectors.toList()),
                comment.getCreateAt()
        );
    }

    public ResponseEntity<?> replyToComment(ReplyCommentRequest replyCommentRequest) {
        Comment parentComment = commentRepository.findById(replyCommentRequest.getParentCommentId())
                .orElseThrow(() -> new ResourceNotFoundException("Parent comment not found"));

        User user = userRepository.findById(replyCommentRequest.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Comic comic = comicRepository.findById(replyCommentRequest.getComicId())
                .orElseThrow(() -> new ResourceNotFoundException("Comic not found"));

        Comment reply = new Comment();
        reply.setContent(replyCommentRequest.getContent());
        reply.setUser(user);
        reply.setComic(comic);
        reply.setParent(parentComment);
        reply.setCreateAt(LocalDateTime.now());

        commentRepository.save(reply);

        return ResponseEntity.ok("Reply added successfully");
    }

    public ResponseEntity<?> deleteComment(Long id, Long userId) {
        Comment comment = commentRepository.findById(id).orElseThrow(() -> new RuntimeException("Không tìm thấy comment"));
        if (!comment.getUser().getId().equals(userId)) {
            throw new RuntimeException("Người dùng không được phép xóa bình luận này");
        }
        commentRepository.deleteById(id);
        return ResponseEntity.ok("Bình luận đã được xóa thành công");
    }
}
