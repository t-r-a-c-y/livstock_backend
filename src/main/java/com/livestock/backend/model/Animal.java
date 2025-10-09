package com.livestock.backend.model;


import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "animals")
@Data
public class Animal {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    private UUID id;

    @Column(name = "tag_id", unique = true)
    private String tagId;

    private String type;

    private String breed;

    private String gender;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "owner_id")
    private UUID ownerId;

    private String status;

    @Column(name = "milk_production")
    private BigDecimal milkProduction;

    private String photo;

    @Column(name = "parent_id")
    private UUID parentId;

    @Column(name = "sale_price")
    private BigDecimal salePrice;

    private String notes;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
}