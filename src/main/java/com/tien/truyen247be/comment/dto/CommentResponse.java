package com.tien.truyen247be.comment.dto;

import com.tien.truyen247be.user.dto.UserResponse;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class CommentResponse {
    private Long id;
    private String content;
    private UserResponse user;
    private List<CommentResponse> replies;
    private LocalDateTime createAt;
}
