// src/main/java/com/livestock/dto/ActivityDto.java
package com.livestock.dto;

import com.livestock.entity.enums.ActivityType;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class ActivityDto {

    // Fields used in response (most are read-only / generated)
    private UUID id;                     // generated → ignore in create request
    private ActivityType type;
    private String description;
    private LocalDate date;
    private BigDecimal amount;
    private BigDecimal cost;
    private String notes;

    // These are usually read-only / system-generated
    private UUID createdById;
    private String createdByName;
    private LocalDateTime createdAt;

    // Input-only or common
    private List<UUID> animalIds;        // required for create/update
}