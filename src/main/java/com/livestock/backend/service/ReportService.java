package com.livestock.backend.service;

import com.livestock.backend.dto.FinancialSummaryDTO;
import com.livestock.backend.dto.ReportDTO;
import com.livestock.backend.dto.ReportGenerateDTO;
import com.livestock.backend.model.Animal;
import com.livestock.backend.model.FinancialRecord;
import com.livestock.backend.repository.AnimalRepository;
import com.livestock.backend.repository.FinancialRecordRepository;
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
    private AnimalRepository animalRepository;

    @Autowired
    private FinancialRecordRepository financialRecordRepository;

    @Transactional(readOnly = true)
    public Page<ReportDTO> getAll(Pageable pageable) {
        logger.info("Fetching all reports");
        return reportRepository.findAll(pageable).map(this::toDTO);
    }

    @Transactional
    public ReportDTO generate(ReportGenerateDTO generateDTO) {
        logger.info("Generating report of type: {}", generateDTO.getType());
        String data = switch (generateDTO.getType()) {
            case "animal_summary" -> generateAnimalSummary(generateDTO);
            case "financial_summary" -> generateFinancialSummary(generateDTO);
            default -> throw new RuntimeException("Unsupported report type: " + generateDTO.getType());
        };

        com.livestock.backend.model.Report report = new com.livestock.backend.model.Report();
        report.setType(generateDTO.getType());
        report.setData(data);
        report.setCreatedAt(LocalDateTime.now());
        report = reportRepository.save(report);
        return toDTO(report);
    }

    @Transactional(readOnly = true)
    public ReportDTO getById(UUID id) {
        logger.info("Fetching report by ID: {}", id);
        com.livestock.backend.model.Report report = reportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Report not found"));
        return toDTO(report);
    }

    private String generateAnimalSummary(ReportGenerateDTO dto) {
        Specification<Animal> spec = (root, query, cb) -> cb.isNull(root.get("deletedAt"));
        if (dto.getEntityType() != null && dto.getEntityId() != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get(dto.getEntityType()), UUID.fromString(dto.getEntityId())));
        }
        long count = animalRepository.count(spec);
        return "{\"totalAnimals\": " + count + "}";
    }

    private String generateFinancialSummary(ReportGenerateDTO dto) {
        Specification<FinancialRecord> spec = (root, query, cb) -> cb.isNull(root.get("deletedAt"));
        if (dto.getStartDate() != null) {
            spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("date"), dto.getStartDate()));
        }
        if (dto.getEndDate() != null) {
            spec = spec.and((root, query, cb) -> cb.lessThanOrEqualTo(root.get("date"), dto.getEndDate()));
        }
        FinancialSummaryDTO summary = financialRecordRepository.getSummary();
        return "{\"totalIncome\": " + summary.getTotalIncome() + ", \"totalExpense\": " + summary.getTotalExpense() + ", \"net\": " + summary.getNet() + "}";
    }

    private ReportDTO toDTO(com.livestock.backend.model.Report report) {
        ReportDTO dto = new ReportDTO();
        dto.setId(report.getId());
        dto.setType(report.getType());
        dto.setData(report.getData());
        dto.setCreatedAt(report.getCreatedAt());
        return dto;
    }
}