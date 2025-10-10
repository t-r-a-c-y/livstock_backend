package com.livestock.backend.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "financial_records")
@Data
public class FinancialRecord {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private String type;

    @Column
    private String category;

    @Column(nullable = false)
    private Double amount;

    @Column
    private String description;

    @Column(nullable = false)
    private LocalDate date;

    @Column(name = "owner_id")
    private UUID ownerId;

    @Column(name = "animal_id")
    private UUID animalId;

    @Column(name = "activity_id")
    private UUID activityId;

    @Column(name = "payment_method")
    private String paymentMethod;

    @Column(name = "receipt_number")
    private String receiptNumber;

    @Column(name = "receipt_image")
    private String receiptImage;

    @Column(name = "created_by")
    private UUID createdBy;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
}