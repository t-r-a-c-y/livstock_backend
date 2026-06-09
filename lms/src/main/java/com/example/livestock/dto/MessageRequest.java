package com.example.livestock.dto;

import jakarta.validation.constraints.NotBlank;

public record MessageRequest(
        Long animalId,
        @NotBlank String subject,
        @NotBlank String messageBody
) {
}
