package com.livestock.backend.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class ReportDTO {
    private UUID id;
    private String type;
    private String data;
    private LocalDateTime createdAt;
}