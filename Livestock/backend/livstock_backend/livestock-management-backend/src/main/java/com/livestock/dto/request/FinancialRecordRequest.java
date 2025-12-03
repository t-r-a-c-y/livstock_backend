// src/main/java/com/livestock/dto/request/FinancialRecordRequest.java
package com.livestock.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class FinancialRecordRequest {
    @NotBlank
    private String type; // income, expense

    @NotBlank
    private String category;

    @NotNull
    private BigDecimal amount;

    @NotBlank
    private String description;

    @NotNull
    private LocalDateTime date;

    private UUID ownerId;
    private UUID animalId;
    private String paymentMethod;
    private String receiptNumber;
    private String receiptImage;

    @NotBlank
    private String createdBy;
}