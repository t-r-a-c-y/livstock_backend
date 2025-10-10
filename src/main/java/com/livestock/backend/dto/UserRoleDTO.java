package com.livestock.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class UserRoleDTO {
    private UUID id;
    @NotNull
    private UUID userId;
    @NotBlank
    private String role;
    private LocalDateTime createdAt;
}