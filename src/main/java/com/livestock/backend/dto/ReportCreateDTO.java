package com.livestock.backend.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;



@Data
public class ReportCreateDTO {
    private String title;
    private String type;
    private String description;
    private LocalDate dateFrom;
    private LocalDate dateTo;
    private Map<String, Object> filters;
    private UUID generatedBy;
}
