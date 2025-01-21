package com.tien.truyen247be.favorite.dto;

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
    private Long views;
    private Long favorites;
    private Long numberOfComment;
}
