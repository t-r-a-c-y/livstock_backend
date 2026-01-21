// src/main/java/com/livestock/dto/ActivityDto.java
package com.livestock.dto;

import com.livestock.entity.enums.ActivityType;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@JsonIgnoreProperties(value = {"id", "createdById", "createdByName", "createdAt"}, allowGetters = true)
public class ActivityDto {

    // Fields used in response (most are read-only / generated)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private UUID id;                     // generated → ignore in create request
    private ActivityType type;
    private String description;
    private LocalDate date;
    private BigDecimal amount;
    private BigDecimal cost;
    private String notes;

    // These are usually read-only / system-generated
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private UUID createdById;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String createdByName;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime createdAt;

    // Input-only or common
    private List<UUID> animalIds;        // required for create/update
}