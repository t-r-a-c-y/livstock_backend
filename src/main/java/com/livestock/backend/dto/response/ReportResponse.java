package com.livestock.backend.dto.response;


import lombok.Data;

import java.util.Date;
import java.util.Map;
import java.util.UUID;

@Data
public class ReportResponse {
    private UUID id;
    private String title;
    private String type;
    private String status;
    private Date dateFrom;
    private Date dateTo;
    private Map<String, Object> data;
    private String generatedBy;
    private Date createdAt;
}