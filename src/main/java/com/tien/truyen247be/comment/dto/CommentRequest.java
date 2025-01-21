package com.tien.truyen247be.comment.dto;

import lombok.Data;

@Data
public class CommentRequest {
    private String content;
    private Long userId;
    private Long comicId;
}
