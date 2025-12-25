// src/main/java/com/livestock/controller/FinancialRecordController.java
package com.livestock.controller;

import com.lowagie.text.DocumentException;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import java.time.LocalDate;
import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import com.lowagie.text.DocumentException;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;

import com.livestock.dto.request.FinancialRecordRequest;
import com.livestock.dto.response.FinancialRecordResponse;
import com.livestock.dto.response.ApiResponse;
import com.livestock.service.FinancialRecordService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/financial-records")
public class FinancialRecordController {

    private final FinancialRecordService financialRecordService;

    // Explicit constructor — fixes injection error
    public FinancialRecordController(FinancialRecordService financialRecordService) {
        this.financialRecordService = financialRecordService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<FinancialRecordResponse>>> getAllRecords(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @RequestParam(required = false) UUID ownerId,
            @RequestParam(required = false) UUID animalId) {

        List<FinancialRecordResponse> records = financialRecordService.getAllFinancialRecords(
                type, category, from, to, ownerId, animalId);
        return ResponseEntity.ok(ApiResponse.success(records));
    }

    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<Object>> getSummary() {
        var summary = financialRecordService.getSummary();
        return ResponseEntity.ok(ApiResponse.success(summary));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<FinancialRecordResponse>> createRecord(@Valid @RequestBody FinancialRecordRequest request) {
        FinancialRecordResponse record = financialRecordService.createFinancialRecord(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(record));
    }

    @GetMapping("/reports/financial/pdf")
    public ResponseEntity<InputStreamResource> exportFinancialPdf() throws DocumentException {
        var summary = financialRecordService.getSummary();

        ByteArrayInputStream bis = pdfReportService.generateFinancialReport(
                summary.totalIncome(),
                summary.totalExpense(),
                summary.profit()
        );

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=financial-summary-" + LocalDate.now() + ".pdf");
        headers.add("Content-Type", "application/pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .body(new InputStreamResource(bis));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<FinancialRecordResponse>> updateRecord(
            @PathVariable UUID id, @Valid @RequestBody FinancialRecordRequest request) {
        FinancialRecordResponse record = financialRecordService.updateFinancialRecord(id, request);
        return ResponseEntity.ok(ApiResponse.success(record));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteRecord(@PathVariable UUID id) {
        financialRecordService.deleteFinancialRecord(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}