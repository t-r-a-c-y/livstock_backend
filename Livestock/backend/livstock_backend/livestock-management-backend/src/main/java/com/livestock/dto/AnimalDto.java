package com.livestock.dto;

import com.livestock.entity.enums.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class AnimalDto {

    private UUID id;
    private String tagId;
    private AnimalType type;
    private String breed;
    private Gender gender;
    private LocalDate dateOfBirth;
    private UUID ownerId;          // instead of full Owner object
    private String ownerName;      // optional: for display convenience
    private UUID parentId;         // parent animal ID
    private String parentTagId;    // optional display
    private AnimalStatus status;
    private BigDecimal milkProduction;
    private String photo;
    private BigDecimal salePrice;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}