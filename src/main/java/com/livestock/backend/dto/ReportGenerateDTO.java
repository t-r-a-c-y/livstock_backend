package com.livestock.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ReportGenerateDTO {
    @NotBlank
    private String type;
    private LocalDate startDate;
    private LocalDate endDate;
    private String entityType;
    private String entityId;
}