package com.livestock.backend.dto.request;

import lombok.Data;

@Data
public class FinancialRequest {
    private String type;
    private Double amount;
    private String description;
    private String animalTagId;
}