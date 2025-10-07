package com.livestock.backend.dto.request;


import lombok.Data;

@Data
public class NotificationRequest {
    private String title;
    private String message;
    private String type;
    private String category;
    private String priority;
    private Boolean actionRequired;
    private String relatedEntityType;
    private String relatedEntityId;
}
