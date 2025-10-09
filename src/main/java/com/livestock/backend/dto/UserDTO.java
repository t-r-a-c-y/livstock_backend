package com.livestock.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class UserDTO {
    private UUID id;
    @NotBlank
    private String email;
    @NotBlank
    private String name;
    private String role;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}