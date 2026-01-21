package com.livestock.entity;

import com.livestock.entity.enums.*;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "notifications")
@Data
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 150)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationCategory category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Priority priority;

    @Column(length = 50)
    private String relatedEntityType;

    private UUID relatedEntityId;

    @Column(nullable = false, columnDefinition = "boolean default false")
    private boolean isRead = false;       // ← Must be exactly this name!

    private LocalDateTime readAt;

    @Column(nullable = false)
    private boolean actionRequired = false;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
}