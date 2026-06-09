package com.example.livestock.dto;

import com.example.livestock.enums.PregnancyStatus;

import java.time.LocalDate;

public record BreedingRecordResponse(
        Long id,
        Long cowId,
        String cowTagNumber,
        Long ownerId,
        LocalDate matingDate,
        String maleAnimalUsed,
        PregnancyStatus pregnancyStatus,
        LocalDate expectedBirthDate,
        LocalDate actualBirthDate,
        String notes,
        boolean active
) {
}
