package com.tien.truyen247be.chapter.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ChapterResponse {
    private Long id;
    private String title;
    private Long chapterNumber;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;
}
