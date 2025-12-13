// src/main/java/com/livestock/dto/response/AnimalResponse.java
package com.livestock.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class AnimalResponse {
    private UUID id;
    private String tagId;
    private String type;
    private String breed;
    private String gender;
    private LocalDate dateOfBirth;
    private UUID ownerId;
    private String ownerName;
    private String status;
    private BigDecimal milk;
    private String photo;
    private UUID parentId;
    private String parentTagId;
    private BigDecimal salePrice;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public void setParentTagId(String parentTagId) {
        this.parentTagId = parentTagId;
    }

    public void setOwnerId(UUID ownerId) {
        this.ownerId = ownerId;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

}