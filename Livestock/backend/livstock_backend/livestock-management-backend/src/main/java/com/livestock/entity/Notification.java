// src/main/java/com/livestock/entity/Notification.java
package com.livestock.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "notifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Notification {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

    @Column(nullable = false, length = 50)
    private String type; // alert, warning, info, success

    @Column(nullable = false, length = 50)
    private String priority = "medium"; // low, medium, high, urgent

    @Column(nullable = false, length = 50)
    private String category; // health, finance, system, reminder

    @Column(name = "is_read")
    private boolean isRead = false;

    @Column(name = "action_required")
    private boolean actionRequired = false;

    @Column(name = "related_entity_id")
    private UUID relatedEntityId;

    @Column(name = "related_entity_type", length = 50)
    private String relatedEntityType; // animal, owner, activity, financial

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "read_at")
    private LocalDateTime readAt;
}