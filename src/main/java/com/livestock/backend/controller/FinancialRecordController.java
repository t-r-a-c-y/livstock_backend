package com.livestock.backend.controller;

import com.livestock.backend.dto.ApiResponse;
import com.livestock.backend.dto.FinancialRecordDTO;
import com.livestock.backend.dto.FinancialRecordCreateDTO;
import com.livestock.backend.dto.FinancialRecordUpdateDTO;
import com.livestock.backend.dto.FinancialSummaryDTO;
import com.livestock.backend.service.FinancialRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/api/financial-records")
@RequiredArgsConstructor
public class FinancialRecordController {
    private final FinancialRecordService financialRecordService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<FinancialRecordDTO>>> getAll(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            Pageable pageable) {
        ApiResponse<Page<FinancialRecordDTO>> response = new ApiResponse<>();
        response.setData(financialRecordService.getAllFinancialRecords(type, startDate, endDate, pageable));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<FinancialRecordDTO>> getById(@PathVariable UUID id) {
        ApiResponse<FinancialRecordDTO> response = new ApiResponse<>();
        response.setData(financialRecordService.getFinancialRecordById(id));
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<FinancialRecordDTO>> create(@Valid @RequestBody FinancialRecordCreateDTO dto) {
        ApiResponse<FinancialRecordDTO> response = new ApiResponse<>();
        response.setData(financialRecordService.createFinancialRecord(dto));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<FinancialRecordDTO>> update(@PathVariable UUID id, @Valid @RequestBody FinancialRecordUpdateDTO dto) {
        ApiResponse<FinancialRecordDTO> response = new ApiResponse<>();
        response.setData(financialRecordService.updateFinancialRecord(id, dto));
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        financialRecordService.softDeleteFinancialRecord(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<FinancialSummaryDTO>> getSummary() {
        ApiResponse<FinancialSummaryDTO> response = new ApiResponse<>();
        response.setData(financialRecordService.getFinancialSummary());
        return ResponseEntity.ok(response);
    }
}