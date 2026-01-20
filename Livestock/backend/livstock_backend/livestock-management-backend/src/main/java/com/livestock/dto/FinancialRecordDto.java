package com.livestock.dto;

import com.livestock.entity.enums.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class FinancialRecordDto {

    private UUID id;
    private FinancialType type;
    private String category;
    private BigDecimal amount;
    private String description;
    private LocalDate date;
    private UUID ownerId;
    private String ownerName;          // optional
    private UUID animalId;
    private String animalTagId;        // optional
    private PaymentMethod paymentMethod;
    private String receiptNumber;
    private String receiptImage;
    private UUID createdById;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}