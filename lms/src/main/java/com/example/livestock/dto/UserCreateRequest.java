package com.example.livestock.dto;

import com.example.livestock.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UserCreateRequest(
        @NotBlank String fullName,
        @Email @NotBlank String email,
        String phoneNumber,
        @Size(min = 8) @NotBlank String password,
        @NotNull Role role
) {
}
