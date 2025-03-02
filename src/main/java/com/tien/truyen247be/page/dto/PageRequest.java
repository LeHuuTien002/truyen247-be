package com.tien.truyen247be.page.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PageRequest {
    private Long id;
    private Long pageNumber;
    private String imageUrl;
}
