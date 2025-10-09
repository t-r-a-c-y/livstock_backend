package com.livestock.backend.model;


import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "profiles")
@Data
public class Profile {
    @Id
    private UUID id;  // Matches auth.users.id, not generated

    private String username;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "avatar_url")
    private String avatarUrl;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
