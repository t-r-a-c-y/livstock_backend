// src/main/java/com/livestock/dto/response/NotificationResponse.java
package com.livestock.dto.response;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class NotificationResponse {
    private UUID id;
    private String title;
    private String message;
    private String type;
    private String priority;
    private String category;
    private boolean isRead;
    private boolean actionRequired;
    private UUID relatedEntityId;
    private String relatedEntityType;
    private LocalDateTime createdAt;
    private LocalDateTime readAt;
}