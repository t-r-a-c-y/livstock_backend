package com.livestock.backend.controller;

import com.livestock.backend.dto.ReportDTO;
import com.livestock.backend.service.ReportService;
import com.livestock.backend.util.ResponseWrapper;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/reports")
public class ReportController {
    private static final Logger logger = LoggerFactory.getLogger(ReportController.class);

    @Autowired
    private ReportService reportService;

    @GetMapping
    public ResponseEntity<ResponseWrapper<Page<ReportDTO>>> getAll(Pageable pageable) {
        logger.info("Received request to fetch all reports");
        Page<ReportDTO> reports = reportService.getAll(pageable);
        return ResponseEntity.ok(new ResponseWrapper<>(reports, null));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseWrapper<ReportDTO>> getById(@PathVariable UUID id) {
        logger.info("Received request to fetch report with id: {}", id);
        try {
            ReportDTO report = reportService.getById(id);
            return ResponseEntity.ok(new ResponseWrapper<>(report, null));
        } catch (RuntimeException e) {
            logger.error("Error fetching report with id: {}", id, e);
            return ResponseEntity.badRequest().body(new ResponseWrapper<>(null, new ResponseWrapper.ErrorMessage(e.getMessage())));
        }
    }

    @PostMapping
    public ResponseEntity<ResponseWrapper<ReportDTO>> generate(@Valid @RequestBody ReportDTO reportDTO) {
        logger.info("Received request to generate report with title: {}", reportDTO.getTitle());
        try {
            ReportDTO generatedReport = reportService.generate(reportDTO);
            return ResponseEntity.ok(new ResponseWrapper<>(generatedReport, null));
        } catch (RuntimeException e) {
            logger.error("Error generating report: {}", e.getMessage());
            return ResponseEntity.badRequest().body(new ResponseWrapper<>(null, new ResponseWrapper.ErrorMessage(e.getMessage())));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseWrapper<Void>> delete(@PathVariable UUID id) {
        logger.info("Received request to delete report with id: {}", id);
        try {
            reportService.delete(id);
            return ResponseEntity.ok(new ResponseWrapper<>(null, null));
        } catch (RuntimeException e) {
            logger.error("Error deleting report with id: {}", id, e);
            return ResponseEntity.badRequest().body(new ResponseWrapper<>(null, new ResponseWrapper.ErrorMessage(e.getMessage())));
        }
    }
}