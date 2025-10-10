package com.livestock.backend.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "animals")
@Data
public class Animal {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "tag_id", nullable = false, unique = true)
    private String tagId;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private String breed;

    @Column(nullable = false)
    private String gender;

    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth;

    @Column(name = "owner_id", nullable = false)
    private UUID ownerId;

    @Column(nullable = false)
    private String status;

    @Column(name = "milk_production")
    private Double milkProduction;

    @Column
    private String photo;

    @Column(name = "parent_id")
    private UUID parentId;

    @Column(name = "sale_price")
    private Double salePrice;

    @Column
    private String notes;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
}