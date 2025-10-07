package com.livestock.backend.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "notifications")
@Data
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String title;
    private String message;
    private String type;
    private String category;
    private String priority;
    private Boolean isRead = false;
    private Boolean actionRequired;
    private String relatedEntityType;
    private UUID relatedEntityId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserProfile user;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt = new Date();
}