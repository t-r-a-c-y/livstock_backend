package com.livestock.backend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class ActivityDTO {
    private UUID id;
    @NotNull
    private String type;
    @NotNull
    private LocalDateTime date;
    @NotNull
    private UUID animalId;
    private String description;
    private BigDecimal cost;
    private BigDecimal amount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
}