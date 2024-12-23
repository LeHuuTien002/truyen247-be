package com.tien.truyen247be.payload.request;

import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ComicRequest {
    private String name;
    private String otherName;
    private String status;
    private String content;
    private String author;
    private boolean activate = false; // Mặc định truyện đang hoạt động
    private String thumbnail; // URL hoặc đường dẫn tới ảnh bìa
    private Set<Long> genreIds = new HashSet<>();
}
