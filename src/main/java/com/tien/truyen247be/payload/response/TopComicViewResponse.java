package com.tien.truyen247be.payload.response;

import lombok.Data;

@Data
public class TopComicViewResponse {
    private Long id;
    private String name;
    private Long views;
    private Long favorites;
    private Long numberOfComment;
    private String thumbnail;
}
