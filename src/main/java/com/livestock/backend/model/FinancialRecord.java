package com.livestock.backend.model;


import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "financial_records")
@Data
public class FinancialRecord {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    private UUID id;

    private String type;

    private LocalDate date;

    private BigDecimal amount;

    private String description;

    @Column(name = "payment_method")
    private String paymentMethod;

    @Column(name = "receipt_number")
    private String receiptNumber;

    @Column(name = "receipt_image")
    private String receiptImage;

    @Column(name = "activity_id")
    private UUID activityId;

    @Column(name = "created_by")
    private UUID createdBy;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
}