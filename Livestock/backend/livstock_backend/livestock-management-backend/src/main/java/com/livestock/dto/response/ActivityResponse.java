// src/main/java/com/livestock/dto/response/ActivityResponse.java
package com.livestock.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class ActivityResponse {
    private UUID id;
    private String type;
    private String description;
    private LocalDateTime date;
    private BigDecimal amount;
    private BigDecimal cost;
    private String notes;
    private String createdBy;
    private LocalDateTime createdAt;
    private List<UUID> animalIds;
    private List<String> animalTagIds;


    public void setAnimalIds(List<UUID> animalIds) {
        this.animalIds = animalIds;
    }

    public void setAnimalTagIds(List<String> animalTagIds) {
        this.animalTagIds = animalTagIds;
    }
}