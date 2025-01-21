package com.tien.truyen247be.chapter.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ChapterRequest {
    private Long id;
    private String title;
    private Long chapterNumber;
}
