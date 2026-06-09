package com.example.livestock.dto;

import com.example.livestock.enums.PregnancyStatus;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record BreedingRecordRequest(
        @NotNull Long cowId,
        @NotNull LocalDate matingDate,
        String maleAnimalUsed,
        PregnancyStatus pregnancyStatus,
        LocalDate expectedBirthDate,
        LocalDate actualBirthDate,
        String notes
) {
}
