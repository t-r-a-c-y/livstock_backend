package com.livestock.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class AnimalDTO {
    private UUID id;
    @NotBlank
    private String tagId;
    @NotBlank
    private String type;
    @NotBlank
    private String breed;
    @NotBlank
    private String gender;
    @NotNull
    private LocalDate dateOfBirth;
    @NotNull
    private UUID ownerId;
    @NotBlank
    private String status;
    private Double milkProduction;
    private String photo;
    private UUID parentId;
    private Double salePrice;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
}