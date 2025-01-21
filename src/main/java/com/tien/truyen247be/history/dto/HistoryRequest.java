package com.tien.truyen247be.history.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class HistoryRequest {
    private Long userId;
    private Long comicId;
    private Long chapterId;
}
