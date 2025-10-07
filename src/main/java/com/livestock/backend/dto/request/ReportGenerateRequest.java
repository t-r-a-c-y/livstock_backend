package com.livestock.backend.dto.request;


import lombok.Data;

import java.util.Date;
import java.util.Map;
import java.util.UUID;

@Data
public class ReportGenerateRequest {
    private String type;
    private String title;
    private String description;
    private Date dateFrom;
    private Date dateTo;
    private Map<String, Object> filters; // e.g., {"animalType": "Cow", "ownerId": "uuid"}
}