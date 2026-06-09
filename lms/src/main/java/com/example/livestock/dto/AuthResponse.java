package com.example.livestock.dto;

import com.example.livestock.enums.Role;

public record AuthResponse(
        String token,
        Long userId,
        String fullName,
        String email,
        Role role
) {
}
