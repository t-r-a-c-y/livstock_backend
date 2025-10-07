package com.livestock.backend.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "user_profiles")
@Data
public class UserProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name;
    private String email;
    private String password;
    private String role;
    private String avatar;
    private String status;

    @OneToOne
    @JoinColumn(name = "owner_id")
    private Owner owner;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt = new Date();
}