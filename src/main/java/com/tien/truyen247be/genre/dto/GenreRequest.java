package com.tien.truyen247be.genre.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class GenreRequest {
    @NotBlank(message = "Tên thể loại không được để trống")
    private String name;
    @NotBlank(message = "Mô tả không được để trống")
    private String description;
}
