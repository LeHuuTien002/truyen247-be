package com.tien.truyen247be.payload.response;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class HistoryResponse {
    private Long id;
    private Long comicId;
    private Long chapterId;
    private String comicName;
    private String comicThumbnail;
    private Long chapterNumber;
}
