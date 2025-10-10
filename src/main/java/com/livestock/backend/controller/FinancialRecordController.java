package com.livestock.backend.controller;

import com.livestock.backend.dto.FinancialRecordDTO;
import com.livestock.backend.dto.FinancialSummaryDTO;
import com.livestock.backend.service.FinancialRecordService;
import com.livestock.backend.util.ResponseWrapper;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/api/financial-records")
public class FinancialRecordController {
    private static final Logger logger = LoggerFactory.getLogger(FinancialRecordController.class);

    @Autowired
    private FinancialRecordService financialRecordService;

    @GetMapping
    public ResponseEntity<ResponseWrapper<Page<FinancialRecordDTO>>> getAll(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo,
            @RequestParam(required = false) UUID animalId,
            Pageable pageable) {
        logger.info("Received request to fetch financial records with type: {}, dateFrom: {}, dateTo: {}, animalId: {}", type, dateFrom, dateTo, animalId);
        Page<FinancialRecordDTO> records = financialRecordService.getAll(type, dateFrom, dateTo, animalId, pageable);
        return ResponseEntity.ok(new ResponseWrapper<>(records, null));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseWrapper<FinancialRecordDTO>> getById(@PathVariable UUID id) {
        logger.info("Received request to fetch financial record with id: {}", id);
        try {
            FinancialRecordDTO record = financialRecordService.getById(id);
            return ResponseEntity.ok(new ResponseWrapper<>(record, null));
        } catch (RuntimeException e) {
            logger.error("Error fetching financial record with id: {}", id, e);
            return ResponseEntity.badRequest().body(new ResponseWrapper<>(null, new ResponseWrapper.ErrorMessage(e.getMessage())));
        }
    }

    @PostMapping
    public ResponseEntity<ResponseWrapper<FinancialRecordDTO>> create(@Valid @RequestBody FinancialRecordDTO recordDTO) {
        logger.info("Received request to create financial record with type: {}", recordDTO.getType());
        try {
            FinancialRecordDTO createdRecord = financialRecordService.create(recordDTO);
            return ResponseEntity.ok(new ResponseWrapper<>(createdRecord, null));
        } catch (RuntimeException e) {
            logger.error("Error creating financial record: {}", e.getMessage());
            return ResponseEntity.badRequest().body(new ResponseWrapper<>(null, new ResponseWrapper.ErrorMessage(e.getMessage())));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseWrapper<FinancialRecordDTO>> update(@PathVariable UUID id, @Valid @RequestBody FinancialRecordDTO recordDTO) {
        logger.info("Received request to update financial record with id: {}", id);
        try {
            FinancialRecordDTO updatedRecord = financialRecordService.update(id, recordDTO);
            return ResponseEntity.ok(new ResponseWrapper<>(updatedRecord, null));
        } catch (RuntimeException e) {
            logger.error("Error updating financial record with id: {}", id, e);
            return ResponseEntity.badRequest().body(new ResponseWrapper<>(null, new ResponseWrapper.ErrorMessage(e.getMessage())));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseWrapper<Void>> delete(@PathVariable UUID id) {
        logger.info("Received request to delete financial record with id: {}", id);
        try {
            financialRecordService.delete(id);
            return ResponseEntity.ok(new ResponseWrapper<>(null, null));
        } catch (RuntimeException e) {
            logger.error("Error deleting financial record with id: {}", id, e);
            return ResponseEntity.badRequest().body(new ResponseWrapper<>(null, new ResponseWrapper.ErrorMessage(e.getMessage())));
        }
    }

    @GetMapping("/summary")
    public ResponseEntity<ResponseWrapper<FinancialSummaryDTO>> getSummary() {
        logger.info("Received request to fetch financial summary");
        try {
            FinancialSummaryDTO summary = financialRecordService.getSummary();
            return ResponseEntity.ok(new ResponseWrapper<>(summary, null));
        } catch (RuntimeException e) {
            logger.error("Error fetching financial summary: {}", e.getMessage());
            return ResponseEntity.badRequest().body(new ResponseWrapper<>(null, new ResponseWrapper.ErrorMessage(e.getMessage())));
        }
    }
}