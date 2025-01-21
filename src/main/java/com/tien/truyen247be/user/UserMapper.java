package com.tien.truyen247be.user;

import com.tien.truyen247be.user.dto.UserResponse;

import java.util.stream.Collectors;

public class UserMapper {
    public static UserResponse toUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .picture(user.getPicture())
                .active(user.isActive())
                .premium(user.isPremium())
                .premiumExpiryDate(user.getPremiumExpiryDate())
                .createAt(user.getCreateAt())
                .updateAt(user.getUpdateAt())
                .registrationType(user.getRegistrationType() != null ? user.getRegistrationType().name() : null)
                .roles(user.getRoles().stream()
                        .map(role -> role.getName().name())
                        .collect(Collectors.toSet()))
                .build();
    }
}
