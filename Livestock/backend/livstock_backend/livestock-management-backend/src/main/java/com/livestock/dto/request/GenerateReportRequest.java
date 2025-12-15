// src/main/java/com/livestock/dto/request/GenerateReportRequest.java
package com.livestock.dto.request;

import java.time.LocalDateTime;
import java.util.Map;

public class GenerateReportRequest {

    private String title;
    private String type;
    private String description;
    private LocalDateTime dateFrom;
    private LocalDateTime dateTo;
    private Map<String, Object> filters;

    // Explicit getters
    public String getTitle() {
        return title;
    }

    public String getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getDateFrom() {
        return dateFrom;
    }

    public LocalDateTime getDateTo() {
        return dateTo;
    }

    public Map<String, Object> getFilters() {
        return filters;
    }

    // Setters (ModelMapper uses them)
    public void setTitle(String title) {
        this.title = title;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDateFrom(LocalDateTime dateFrom) {
        this.dateFrom = dateFrom;
    }

    public void setDateTo(LocalDateTime dateTo) {
        this.dateTo = dateTo;
    }

    public void setFilters(Map<String, Object> filters) {
        this.filters = filters;
    }
}