// src/main/java/com/livestock/dto/request/GenerateReportRequest.java
package com.livestock.dto.request;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Data
public class GenerateReportRequest {
    private String title;
    private String type;
    private String description;
    private LocalDateTime dateFrom;
    private LocalDateTime dateTo;
    private Map<String, Object> filters;
}