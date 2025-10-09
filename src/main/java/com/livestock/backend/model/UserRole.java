package com.livestock.backend.model;



import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "user_roles")
@Data
public class UserRole {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    private UUID id;

    @Column(name = "user_id")
    private UUID userId;

    private String role;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}