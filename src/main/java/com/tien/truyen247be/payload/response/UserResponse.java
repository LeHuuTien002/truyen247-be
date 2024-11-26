package com.tien.truyen247be.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private Set<String> roles;

    public UserResponse(Long id, String username,String picture) {
        this.id = id;
        this.username = username;
        this.picture = picture;
    }

}
