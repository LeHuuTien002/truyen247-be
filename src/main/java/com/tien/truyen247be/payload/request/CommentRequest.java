package com.tien.truyen247be.payload.request;

import lombok.Data;

@Data
public class CommentRequest {
    private String content;
    private Long userId;
    private Long comicId;
}
