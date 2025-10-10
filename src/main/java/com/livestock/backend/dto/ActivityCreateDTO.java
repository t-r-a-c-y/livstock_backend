package com.livestock.backend.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;



@Data
public class ActivityCreateDTO {
    private UUID[] animalIds;
    private String type;
    private String description;
    private LocalDate date;
    private Double amount;
    private Double cost;
    private String notes;
    private UUID createdBy;
}

