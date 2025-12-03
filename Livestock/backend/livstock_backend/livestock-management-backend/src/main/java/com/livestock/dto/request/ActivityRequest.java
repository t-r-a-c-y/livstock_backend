// src/main/java/com/livestock/dto/request/ActivityRequest.java
package com.livestock.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class ActivityRequest {
    @NotNull
    private List<UUID> animalIds;

    @NotBlank
    private String type;

    @NotBlank
    private String description;

    @NotNull
    private LocalDateTime date;

    private BigDecimal amount;
    private BigDecimal cost;
    private String notes;

    @NotBlank
    private String createdBy; // user ID or name
}