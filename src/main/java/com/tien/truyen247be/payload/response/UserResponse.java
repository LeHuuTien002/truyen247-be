package com.tien.truyen247be.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponse {
    private Long id;
    private String username;
    private String email;
    private String picture;
    private String registrationType;
    private boolean active;
    private boolean premium;
    private LocalDate premiumExpiryDate;
    private Set<String> roles;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;

    public UserResponse(Long id, String username,String picture) {
        this.id = id;
        this.username = username;
        this.picture = picture;
    }

}
