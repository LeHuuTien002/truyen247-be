package com.tien.truyen247be.payload.request;

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
