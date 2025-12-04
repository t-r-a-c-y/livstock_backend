// src/main/java/com/livestock/service/ReportService.java
package com.livestock.service;

import com.livestock.dto.request.GenerateReportRequest;
import com.livestock.dto.response.ApiResponse;
import com.livestock.entity.Report;
import com.livestock.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;
    private final ModelMapper modelMapper;

    public List<Report> getAllReports() {
        return reportRepository.findTop10ByOrderByCreatedAtDesc();
    }

    public Report getReportById(UUID id) {
        return reportRepository.findById(id).orElse(null);
    }

    public Report generateReport(GenerateReportRequest request, String generatedBy) {
        Report report = new Report();
        report.setTitle(request.getTitle());
        report.setType(request.getType());
        report.setDescription(request.getDescription());
        report.setDateFrom(request.getDateFrom());
        report.setDateTo(request.getDateTo());
        report.setFilters(request.getFilters());
        report.setGeneratedBy(generatedBy);
        report.setStatus("pending");

        // Simulate async report generation (in real app: use @Async + background job)
        // For now, we set dummy data
        report.setData(Map.of(
                "message", "Report generation completed",
                "generatedAt", LocalDateTime.now().toString(),
                "totalRecords", 123
        ));
        report.setStatus("generated");

        return reportRepository.save(report);
    }
}