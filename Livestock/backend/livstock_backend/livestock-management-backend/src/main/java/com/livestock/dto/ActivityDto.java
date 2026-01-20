package com.livestock.dto;

import com.livestock.entity.enums.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class ActivityDto {

    private UUID id;
    private ActivityType type;
    private String description;
    private LocalDate date;
    private BigDecimal amount;
    private BigDecimal cost;
    private String notes;
    private UUID createdById;          // user who created
    private String createdByName;      // optional
    private LocalDateTime createdAt;
    private List<UUID> animalIds;      // list of involved animals
}