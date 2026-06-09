package com.example.livestock.dto;

import com.example.livestock.enums.AccountStatus;
import com.example.livestock.enums.Role;

public record UserResponse(
        Long id,
        String fullName,
        String email,
        String phoneNumber,
        Role role,
        AccountStatus accountStatus,
        boolean active
) {
}
