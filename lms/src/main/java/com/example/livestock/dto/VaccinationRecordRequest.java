package com.example.livestock.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record VaccinationRecordRequest(
        @NotNull Long animalId,
        @NotBlank String vaccineName,
        @NotNull LocalDate vaccinationDate,
        LocalDate nextDueDate,
        String administeredBy,
        String notes
) {
}
