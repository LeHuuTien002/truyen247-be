package com.tien.truyen247be.payload.response;

import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ComicResponse {
    private Long id;
    private String name;
    private String otherName;
    private String status;
    private String content;
    private String author;
    private Long views;
    private Long favorites;
    private Long numberOfComment;
    private boolean activate;
    private String thumbnail;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;
    private Set<GenreListByComicIdResponse> genres;

    @Data
    public static class GenreListByComicIdResponse{
        private Long id;
        private String name;
    }
}
