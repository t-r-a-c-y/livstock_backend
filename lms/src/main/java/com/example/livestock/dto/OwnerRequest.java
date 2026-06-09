package com.example.livestock.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record OwnerRequest(
        @Valid @NotNull UserCreateRequest user,
        @NotBlank String nationalId,
        @NotBlank String address
) {
}
