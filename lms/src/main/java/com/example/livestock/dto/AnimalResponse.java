package com.example.livestock.dto;

import com.example.livestock.enums.AnimalStatus;
import com.example.livestock.enums.AnimalType;
import com.example.livestock.enums.Gender;

import java.math.BigDecimal;
import java.time.LocalDate;

public record AnimalResponse(
        Long id,
        Long ownerId,
        String ownerName,
        String tagNumber,
        AnimalType animalType,
        String breed,
        Gender gender,
        LocalDate dateOfBirth,
        String color,
        BigDecimal weight,
        AnimalStatus animalStatus,
        boolean active
) {
}
