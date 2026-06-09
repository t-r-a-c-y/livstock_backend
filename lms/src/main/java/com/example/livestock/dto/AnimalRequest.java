package com.example.livestock.dto;

import com.example.livestock.enums.AnimalStatus;
import com.example.livestock.enums.AnimalType;
import com.example.livestock.enums.Gender;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record AnimalRequest(
        @NotNull Long ownerId,
        @NotBlank String tagNumber,
        @NotNull AnimalType animalType,
        String breed,
        @NotNull Gender gender,
        LocalDate dateOfBirth,
        String color,
        BigDecimal weight,
        AnimalStatus animalStatus
) {
}
