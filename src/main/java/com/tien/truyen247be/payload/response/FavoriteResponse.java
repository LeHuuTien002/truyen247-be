package com.tien.truyen247be.payload.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FavoriteResponse {
    private Long comicId;
    private String comicName;
    private String comicThumbnail;
}
