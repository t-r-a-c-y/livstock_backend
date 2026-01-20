package com.livestock.dto;

import com.livestock.entity.enums.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Data
public class ReportDto {

    private UUID id;
    private String title;
    private String description;
    private ReportType type;
    private LocalDate dateFrom;
    private LocalDate dateTo;
    private Map<String, Object> filters;
    private Map<String, Object> data;      // the generated report content
    private ReportStatus status;
    private UUID generatedById;
    private String generatedByName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}