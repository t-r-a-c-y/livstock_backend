package com.livestock.backend.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.livestock.backend.model.Report;
import com.livestock.backend.repository.ReportRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class ReportService {
    private static final Logger logger = LoggerFactory.getLogger(ReportService.class);

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AnimalService animalService;  // For generating data

    @Autowired
    private FinancialRecordService financialRecordService;

    // Add other services for data generation

    @Transactional(readOnly = true)
    public Page<ReportDTO> getAll(Pageable pageable) {
        logger.info("Fetching reports");
        Specification<Report> spec = Specification.where((root, query, cb) -> cb.isNull(root.get("deletedAt")));
        return reportRepository.findAll(spec, pageable).map(this::toDTO);
    }

    @Transactional
    public ReportDTO generate(ReportGenerateDTO dto) {
        logger.info("Generating report of type: {}", dto.getType());
        Report report = new Report();
        report.setType(dto.getType());
        report.setGeneratedBy(getCurrentUserId());
        report.setDateFrom(dto.getDateFrom());
        report.setDateTo(dto.getDateTo());
        report.setCreatedAt(LocalDateTime.now());

        // Generate data based on type
        Object generatedData;
        if ("animal_stats".equals(dto.getType())) {
            generatedData = animalService.getStats();
        } else if ("financial_summary".equals(dto.getType())) {
            generatedData = financialRecordService.getSummary();
        } else {
            throw new RuntimeException("Unknown report type");
        }

        try {
            report.setData(objectMapper.writeValueAsString(generatedData));
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize report data");
        }

        report = reportRepository.save(report);
        return toDTO(report);
    }

    @Transactional(readOnly = true)
    public ReportDTO getById(UUID id) {
        logger.info("Fetching report by ID: {}", id);
        Report report = reportRepository.findById(id).orElseThrow(() -> new RuntimeException("Report not found"));
        if (report.getDeletedAt() != null) throw new RuntimeException("Report deleted");
        return toDTO(report);
    }

    @Transactional
    public void softDelete(UUID id) {
        logger.info("Soft deleting report: {}", id);
        Report report = reportRepository.findById(id).orElseThrow(() -> new RuntimeException("Report not found"));
        report.setDeletedAt(LocalDateTime.now());
        reportRepository.save(report);
    }

    private ReportDTO toDTO(Report report) {
        ReportDTO dto = new ReportDTO();
        dto.setId(report.getId());
        dto.setType(report.getType());
        dto.setGeneratedBy(report.getGeneratedBy());
        dto.setDateFrom(report.getDateFrom());
        dto.setDateTo(report.getDateTo());
        dto.setData(report.getData());
        dto.setCreatedAt(report.getCreatedAt());
        return dto;
    }

    private UUID getCurrentUserId() {
        // same as above
    }
}