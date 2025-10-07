package com.livestock.backend.dto.response;

import lombok.Data;

import java.util.Date;
import java.util.UUID;

@Data
public class FinancialResponse {
    private UUID id;
    private String type;
    private String category;
    private Double amount;
    private Date date;
    private String description;
    private String paymentMethod;
    private String receiptNumber;
    private String receiptImage;
    private OwnerSummary owner;
    private AnimalSummary animal;
    private Date createdAt;

    @Data
    public static class OwnerSummary {
        private UUID id;
        private String name;
    }

    @Data
    public static class AnimalSummary {
        private UUID id;
        private String tagId;
    }
}
