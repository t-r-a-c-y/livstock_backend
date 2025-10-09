package com.livestock.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class OwnerDTO {
    private UUID id;
    @NotBlank
    private String name;
    @NotBlank
    private String phone;
    private String email;
    private String address;
    private String nationalId;
    private String bankAccount;
    private String emergencyContact;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
}