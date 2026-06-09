package com.example.livestock.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record HealthRecordRequest(
        @NotNull Long animalId,
        @NotBlank String diagnosis,
        String treatment,
        String medication,
        String veterinarianName,
        @NotNull LocalDate visitDate,
        LocalDate nextVisitDate,
        String notes
) {
}
