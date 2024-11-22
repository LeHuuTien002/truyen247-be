package com.tien.truyen247be.payload.request;

import lombok.Data;

@Data
public class ReplyCommentRequest {
    private Long parentCommentId; // ID của bình luận cha
    private Long comicId;         // ID của truyện
    private Long userId;          // ID của người dùng
    private String content;       // Nội dung bình luận
}
