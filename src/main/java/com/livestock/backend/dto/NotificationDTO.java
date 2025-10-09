package com.livestock.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class NotificationDTO {
    private UUID id;
    @NotBlank
    private String type;
    @NotBlank
    private String message;
    private boolean isRead;
    private UUID userId;
    private LocalDateTime createdAt;
}