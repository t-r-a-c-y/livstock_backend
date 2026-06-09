package com.example.livestock.dto;

import com.example.livestock.enums.AnimalStatus;
import com.example.livestock.enums.AnimalType;
import com.example.livestock.enums.ExportFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record ReportRequest(
        @NotBlank String reportType,
        @NotNull ExportFormat exportFormat,
        LocalDate fromDate,
        LocalDate toDate,
        Long ownerId,
        AnimalType animalType,
        AnimalStatus status,
        boolean includeInactive
) {
}
