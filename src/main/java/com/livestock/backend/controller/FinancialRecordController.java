package com.livestock.backend.controller;

import com.livestock.backend.dto.FinancialRecordDTO;
import com.livestock.backend.dto.FinancialSummaryDTO;
import com.livestock.backend.dto.ApiResponse;
import com.livestock.backend.service.FinancialRecordService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/api/financial-records")
public class FinancialRecordController {
    @Autowired
    private FinancialRecordService financialRecordService;

    @GetMapping
    public ApiResponse<Page<FinancialRecordDTO>> list(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            Pageable pageable) {
        return new ApiResponse<>(financialRecordService.getAll(type, startDate, endDate, pageable), null);
    }

    @GetMapping("/{id}")
    public ApiResponse<FinancialRecordDTO> get(@PathVariable UUID id) {
        return new ApiResponse<>(financialRecordService.getById(id), null);
    }

    @PostMapping
    public ApiResponse<FinancialRecordDTO> create(@Valid @RequestBody FinancialRecordDTO dto) {
        return new ApiResponse<>(financialRecordService.create(dto), null);
    }

    @PutMapping("/{id}")
    public ApiResponse<FinancialRecordDTO> update(@PathVariable UUID id, @Valid @RequestBody FinancialRecordDTO dto) {
        return new ApiResponse<>(financialRecordService.update(id, dto), null);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable UUID id) {
        financialRecordService.softDelete(id);
        return new ApiResponse<>(null, null);
    }

    @GetMapping("/summary")
    public ApiResponse<FinancialSummaryDTO> summary() {
        return new ApiResponse<>(financialRecordService.getSummary(), null);
    }
}