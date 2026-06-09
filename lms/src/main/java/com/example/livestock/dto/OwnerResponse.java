package com.example.livestock.dto;

public record OwnerResponse(
        Long id,
        UserResponse user,
        String nationalId,
        String address,
        boolean active
) {
}
