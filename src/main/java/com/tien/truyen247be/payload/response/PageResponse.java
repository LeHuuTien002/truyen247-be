package com.tien.truyen247be.payload.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PageResponse {
    private Long id;
    private Long pageNumber;
    private String imageUrl;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;
}
