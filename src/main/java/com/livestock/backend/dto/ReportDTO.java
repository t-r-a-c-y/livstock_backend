package com.livestock.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class ReportDTO {
    private UUID id;
    @NotBlank
    private String title;
    @NotBlank
    private String type;
    private String description;
    @NotBlank
    private String status;
    private LocalDate dateFrom;
    private LocalDate dateTo;
    private String data;
    private String filters;
    private UUID generatedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}