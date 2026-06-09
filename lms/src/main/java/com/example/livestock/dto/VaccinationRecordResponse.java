package com.example.livestock.dto;

import java.time.LocalDate;

public record VaccinationRecordResponse(
        Long id,
        Long animalId,
        String animalTagNumber,
        String vaccineName,
        LocalDate vaccinationDate,
        LocalDate nextDueDate,
        String administeredBy,
        String notes,
        boolean active
) {
}
