package com.tien.truyen247be.comment;

import com.tien.truyen247be.comment.dto.CommentRequest;
import com.tien.truyen247be.comment.dto.ReplyCommentRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class CommentController {
    @Autowired
    private CommentService commentService;

    @PostMapping("/comments")
    public ResponseEntity<?> addComment(@RequestBody CommentRequest commentRequest) {
        return commentService.addComment(commentRequest);
    }

    @PostMapping("/comments/reply")
    public ResponseEntity<?> replyToComment(@RequestBody ReplyCommentRequest replyCommentRequest) {
        return commentService.replyToComment(replyCommentRequest);
    }

    @DeleteMapping("/comments/{id}")
    public ResponseEntity<?> deleteComment(@PathVariable Long id, @RequestParam Long userId) {
        return commentService.deleteComment(id, userId);
    }
}
