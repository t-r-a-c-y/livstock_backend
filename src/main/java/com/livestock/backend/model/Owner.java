package com.livestock.backend.model;


import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "owners")
@Data
public class Owner {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    private UUID id;

    private String name;

    private String phone;

    private String email;

    private String address;

    @Column(name = "national_id")
    private String nationalId;

    @Column(name = "bank_account")
    private String bankAccount;

    @Column(name = "emergency_contact")
    private String emergencyContact;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime updatedAt;
}