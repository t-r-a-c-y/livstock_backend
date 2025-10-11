package com.livestock.backend.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;




@Data
public class NotificationUpdateDTO {
    private String title;
    private String message;
    private String type;
    private String priority;
    private String category;
    private Boolean isRead;
    private Boolean actionRequired;
    private UUID relatedEntityId;
    private String relatedEntityType;
    private LocalDateTime readAt;
}