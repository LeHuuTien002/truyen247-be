package com.tien.truyen247be.auth.payload;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class OAuth2LoginRequest {
    private String idToken;
}

