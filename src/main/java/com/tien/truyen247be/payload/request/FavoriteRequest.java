package com.tien.truyen247be.payload.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FavoriteRequest {
    private Long id;
    private Long userId;
    private Long comicId;
}
