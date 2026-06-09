package com.example.livestock.dto;

import java.time.LocalDateTime;

public record AuditLogResponse(
        Long id,
        String action,
        String entityType,
        Long entityId,
        String actor,
        String details,
        LocalDateTime createdAt
) {
}
