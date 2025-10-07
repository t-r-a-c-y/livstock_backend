package com.livestock.backend.dto.response;


import lombok.Data;

import java.util.Date;
import java.util.UUID;

@Data
public class NotificationResponse {
    private UUID id;
    private String title;
    private String message;
    private String type;
    private String category;
    private String priority;
    private Boolean isRead;
    private Boolean actionRequired;
    private String relatedEntityType;
    private UUID relatedEntityId;
    private Date createdAt;
}