// src/main/java/com/livestock/dto/request/AnimalRequest.java
package com.livestock.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class AnimalRequest {
    @NotBlank
    private String tagId;

    @NotBlank
    private String type; // cow, calf, goat, kid

    @NotBlank
    private String breed;

    @NotBlank
    private String gender; // male, female

    @NotNull
    private LocalDate dateOfBirth;

    @NotNull
    private UUID ownerId;

    private String status; // healthy, sick, sold, dead

    private BigDecimal milk;

    private String photo;

    private UUID parentId;

    private BigDecimal salePrice;   // ← FIXED HERE

    private String notes;
}