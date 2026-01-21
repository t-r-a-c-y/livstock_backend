package com.livestock.controller;

import com.livestock.dto.ApiResponse;
import com.livestock.dto.ReportDto;
import com.livestock.service.ReportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @PostMapping("/generate")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<ApiResponse<ReportDto>> generateReport(
            @Valid @RequestBody ReportDto dto,
            Authentication authentication) {

        UUID currentUserId = UUID.fromString(authentication.getName());
        ReportDto report = reportService.createReportRequest(dto, currentUserId);
        return ResponseEntity.ok(ApiResponse.success(report, "Report generation started"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','STAFF','VIEWER')")
    public ResponseEntity<ApiResponse<ReportDto>> getReportById(@PathVariable UUID id) {
        ReportDto report = reportService.getReportById(id);
        return ResponseEntity.ok(ApiResponse.success(report));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','STAFF')")
    public ResponseEntity<ApiResponse<List<ReportDto>>> getAllReports() {
        List<ReportDto> reports = reportService.getAllReports();
        return ResponseEntity.ok(ApiResponse.success(reports));
    }

    @GetMapping("/my-reports")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<ReportDto>>> getMyReports(Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        // Assuming ReportService has getReportsByGeneratedBy(userId)
        // List<ReportDto> myReports = reportService.getReportsByGeneratedBy(userId);
        // return ResponseEntity.ok(ApiResponse.success(myReports));
        return ResponseEntity.ok(ApiResponse.success(List.of())); // placeholder
    }
}