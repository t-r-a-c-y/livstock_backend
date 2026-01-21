package com.livestock.controller;

import com.livestock.dto.FinancialRecordDto;
import com.livestock.dto.ApiResponse;
import com.livestock.service.FinancialRecordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/financial-records")
@RequiredArgsConstructor
public class FinancialRecordController {

    private final FinancialRecordService financialRecordService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','STAFF')")
    public ResponseEntity<ApiResponse<FinancialRecordDto>> createRecord(
            @Valid @RequestBody FinancialRecordDto dto,
            Authentication authentication) {

        UUID currentUserId = UUID.fromString(authentication.getName());
        FinancialRecordDto created = financialRecordService.createFinancialRecord(dto, currentUserId);
        return ResponseEntity.ok(ApiResponse.success(created));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','STAFF')")
    public ResponseEntity<ApiResponse<List<FinancialRecordDto>>> getAllRecords() {
        return ResponseEntity.ok(ApiResponse.success(financialRecordService.getAllFinancialRecords()));
    }

    @GetMapping("/owner/{ownerId}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','STAFF')")
    public ResponseEntity<ApiResponse<List<FinancialRecordDto>>> getByOwner(@PathVariable UUID ownerId) {
        return ResponseEntity.ok(ApiResponse.success(financialRecordService.getByOwner(ownerId)));
    }
}