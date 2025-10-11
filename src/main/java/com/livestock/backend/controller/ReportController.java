package com.livestock.backend.controller;

import com.livestock.backend.dto.ApiResponse;
import com.livestock.backend.dto.ReportDTO;
import com.livestock.backend.dto.ReportCreateDTO;
import com.livestock.backend.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {
    private final ReportService reportService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<ReportDTO>>> getAll(Pageable pageable) {
        ApiResponse<Page<ReportDTO>> response = new ApiResponse<>();
        response.setData(reportService.getAllReports(pageable));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ReportDTO>> getById(@PathVariable UUID id) {
        ApiResponse<ReportDTO> response = new ApiResponse<>();
        response.setData(reportService.getReportById(id));
        return ResponseEntity.ok(response);
    }

    @PostMapping("/generate")
    public ResponseEntity<ApiResponse<ReportDTO>> generate(@Valid @RequestBody ReportCreateDTO dto) {
        ApiResponse<ReportDTO> response = new ApiResponse<>();
        response.setData(reportService.generateReport(dto));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        reportService.deleteReport(id);
        return ResponseEntity.noContent().build();
    }
}