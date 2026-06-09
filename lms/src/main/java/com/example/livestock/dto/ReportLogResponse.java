package com.example.livestock.dto;

import com.example.livestock.enums.ExportFormat;

import java.time.LocalDateTime;

public record ReportLogResponse(
        Long id,
        String reportName,
        String reportType,
        ExportFormat exportFormat,
        String generatedBy,
        LocalDateTime generatedAt,
        String filePath
) {
}
