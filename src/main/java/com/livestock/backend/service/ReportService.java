package com.livestock.backend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.livestock.backend.dto.FinancialSummaryDTO;
import com.livestock.backend.dto.ReportDTO;
import com.livestock.backend.model.Report;
import com.livestock.backend.repository.ReportRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Service
public class ReportService {
    private static final Logger logger = LoggerFactory.getLogger(ReportService.class);

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private FinancialRecordService financialRecordService;

    @Autowired
    private ObjectMapper objectMapper;

    @Transactional(readOnly = true)
    public Page<ReportDTO> getAll(Pageable pageable) {
        logger.info("Fetching all reports");
        return reportRepository.findAll(pageable).map(this::toDTO);
    }

    @Transactional(readOnly = true)
    public ReportDTO getById(UUID id) {
        logger.info("Fetching report with id: {}", id);
        Report report = reportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Report not found"));
        return toDTO(report);
    }

    @Transactional
    public ReportDTO generate(ReportDTO dto) {
        logger.info("Generating report with title: {}", dto.getTitle());
        Report report = new Report();
        updateEntityFromDTO(report, dto);
        report.setStatus("generated");
        report.setCreatedAt(LocalDateTime.now());
        report.setUpdatedAt(LocalDateTime.now());
        if ("financial".equals(dto.getType())) {
            try {
                FinancialSummaryDTO summary = financialRecordService.getSummary();
                Map<String, Double> summaryMap = Map.of(
                        "income", summary.getIncome(),
                        "expense", summary.getExpense()
                );
                report.setData(objectMapper.writeValueAsString(summaryMap));
            } catch (Exception e) {
                logger.error("Error serializing financial summary: {}", e.getMessage());
                throw new RuntimeException("Failed to serialize financial summary", e);
            }
        }
        report = reportRepository.save(report);
        return toDTO(report);
    }

    @Transactional
    public void delete(UUID id) {
        logger.info("Deleting report with id: {}", id);
        Report report = reportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Report not found"));
        reportRepository.delete(report);
    }

    private ReportDTO toDTO(Report report) {
        ReportDTO dto = new ReportDTO();
        dto.setId(report.getId());
        dto.setTitle(report.getTitle());
        dto.setType(report.getType());
        dto.setDescription(report.getDescription());
        dto.setStatus(report.getStatus());
        dto.setDateFrom(report.getDateFrom());
        dto.setDateTo(report.getDateTo());
        dto.setData(report.getData());
        dto.setFilters(report.getFilters());
        dto.setGeneratedBy(report.getGeneratedBy());
        dto.setCreatedAt(report.getCreatedAt());
        dto.setUpdatedAt(report.getUpdatedAt());
        return dto;
    }

    private void updateEntityFromDTO(Report report, ReportDTO dto) {
        report.setTitle(dto.getTitle());
        report.setType(dto.getType());
        report.setDescription(dto.getDescription());
        report.setStatus(dto.getStatus());
        report.setDateFrom(dto.getDateFrom());
        report.setDateTo(dto.getDateTo());
        report.setData(dto.getData());
        report.setFilters(dto.getFilters());
        report.setGeneratedBy(dto.getGeneratedBy());
    }
}