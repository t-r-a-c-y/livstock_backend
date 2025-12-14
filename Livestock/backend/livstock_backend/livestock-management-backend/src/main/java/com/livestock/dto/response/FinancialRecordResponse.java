// src/main/java/com/livestock/dto/response/FinancialRecordResponse.java
package com.livestock.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class FinancialRecordResponse {
    private UUID id;
    private String type;
    private String category;
    private BigDecimal amount;
    private String description;
    private LocalDateTime date;
    private UUID ownerId;
    private String ownerName;
    private UUID animalId;
    private String animalTagId;
    private String paymentMethod;
    private String receiptNumber;
    private String receiptImage;
    private String createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public void setOwnerId(UUID ownerId) {
        this.ownerId = ownerId;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public void setAnimalId(UUID animalId) {
        this.animalId = animalId;
    }

    public void setAnimalTagId(String animalTagId) {
        this.animalTagId = animalTagId;
    }


}