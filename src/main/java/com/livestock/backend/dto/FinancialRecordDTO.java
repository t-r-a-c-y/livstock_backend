package com.livestock.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class FinancialRecordDTO {
    private UUID id;
    @NotBlank
    private String type;
    @NotNull
    private BigDecimal amount;
    @NotNull
    private LocalDate date;
    private String description;
    private UUID activityId;
    private UUID createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
}