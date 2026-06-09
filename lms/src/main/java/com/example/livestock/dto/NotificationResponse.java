package com.example.livestock.dto;

import com.example.livestock.enums.NotificationType;

import java.time.LocalDateTime;

public record NotificationResponse(
        Long id,
        String title,
        String message,
        NotificationType notificationType,
        boolean readStatus,
        LocalDateTime createdAt
) {
}
