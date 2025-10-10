package com.livestock.backend.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;




@Data
public class ReportUpdateDTO {
    private String title;
    private String type;
    private String description;
    private String status;
    private LocalDate dateFrom;
    private LocalDate dateTo;
    private Map<String, Object> data;
    private Map<String, Object> filters;
    private UUID generatedBy;
}