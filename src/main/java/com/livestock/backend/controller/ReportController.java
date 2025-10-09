package com.livestock.backend.controller;


import com.livestock.backend.dto.ReportDTO;
import com.livestock.backend.dto.ReportGenerateDTO;
import com.livestock.backend.dto.ApiResponse;
import com.livestock.backend.service.ReportService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/reports")
public class ReportController {
    @Autowired
    private ReportService reportService;

    @GetMapping
    public ApiResponse<Page<ReportDTO>> list(Pageable pageable) {
        return new ApiResponse<>(reportService.getAll(pageable), null);
    }

    @PostMapping("/generate")
    public ApiResponse<ReportDTO> generate(@Valid @RequestBody ReportGenerateDTO dto) {
        return new ApiResponse<>(reportService.generate(dto), null);
    }

    @GetMapping("/{id}")
    public ApiResponse<ReportDTO> get(@PathVariable UUID id) {
        return new ApiResponse<>(reportService.getById(id), null);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable UUID id) {
        reportService.softDelete(id);
        return new ApiResponse<>(null, null);
    }
}