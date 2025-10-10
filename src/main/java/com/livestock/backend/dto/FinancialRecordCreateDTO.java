package com.livestock.backend.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;


@Data
public class FinancialRecordCreateDTO {
    private String type;
    private String category;
    private Double amount;
    private String description;
    private LocalDate date;
    private UUID ownerId;
    private UUID animalId;
    private String paymentMethod;
    private String receiptNumber;
    private String receiptImage;
    private UUID createdBy;
}

