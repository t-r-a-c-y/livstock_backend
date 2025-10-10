package com.livestock.backend.dto;


import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;


@Data
public class AnimalUpdateDTO {
    private String tagId;
    private String type;
    private String breed;
    private String gender;
    private LocalDate dateOfBirth;
    private UUID ownerId;
    private String status;
    private Double milkProduction;
    private String photo;
    private UUID parentId;
    private Double salePrice;
    private String notes;
}