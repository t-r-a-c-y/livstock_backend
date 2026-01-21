package com.livestock.dto;

import com.livestock.entity.enums.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class NotificationDto {

    private UUID id;
    private String title;
    private String message;
    private NotificationType type;
    private NotificationCategory category;
    private Priority priority;
    private String relatedEntityType;
    private UUID relatedEntityId;

    private boolean isRead;               // ← Add / fix this field!

    private LocalDateTime readAt;
    private boolean actionRequired;
    private LocalDateTime createdAt;

    public void setIsRead(boolean isRead) {
        this.isRead = isRead;
    }
}