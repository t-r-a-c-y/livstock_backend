package com.livestock.backend.dto.request;


import lombok.Data;

import java.util.Date;
import java.util.UUID;

@Data
public class FinancialRequest {
    private String type;
    private String category;
    private Double amount;
    private Date date;
    private String description;
    private String paymentMethod;
    private String receiptNumber;
    private UUID ownerId;
    private UUID animalId;
}