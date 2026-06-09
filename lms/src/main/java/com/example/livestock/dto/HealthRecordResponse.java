package com.example.livestock.dto;

import java.time.LocalDate;

public record HealthRecordResponse(
        Long id,
        Long animalId,
        String animalTagNumber,
        String diagnosis,
        String treatment,
        String medication,
        String veterinarianName,
        LocalDate visitDate,
        LocalDate nextVisitDate,
        String notes,
        boolean active
) {
}
