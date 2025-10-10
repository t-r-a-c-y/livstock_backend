package com.livestock.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class ActivityDTO {
    private UUID id;
    private UUID[] animalIds;
    @NotBlank
    private String type;
    private String description;
    @NotNull
    private LocalDate date;
    private Double amount;
    private Double cost;
    private String notes;
    private UUID createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime deletedAt;
}