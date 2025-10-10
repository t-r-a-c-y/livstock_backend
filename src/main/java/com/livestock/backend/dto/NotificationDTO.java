package com.livestock.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class NotificationDTO {
    private UUID id;
    @NotBlank
    private String title;
    @NotBlank
    private String message;
    @NotBlank
    private String type;
    @NotBlank
    private String priority;
    @NotBlank
    private String category;
    private boolean isRead;
    private boolean actionRequired;
    private UUID relatedEntityId;
    private String relatedEntityType;
    private UUID userId;
    private LocalDateTime createdAt;
    private LocalDateTime readAt;
    private LocalDateTime deletedAt;

    public boolean getIsRead() {
        return isRead;
    }

    public void setIsRead(boolean isRead) {
        this.isRead = isRead;
    }
}