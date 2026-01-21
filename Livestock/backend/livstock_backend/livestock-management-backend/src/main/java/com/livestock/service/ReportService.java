// ReportService.java
package com.livestock.service;

import com.livestock.dto.ReportDto;
import com.livestock.entity.Report;
import com.livestock.entity.User;
import com.livestock.entity.enums.ReportStatus;
import com.livestock.entity.enums.ReportType;
import com.livestock.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;
    private final AnimalRepository animalRepository;
    private final FinancialRecordRepository financialRecordRepository;
    private final UserService userService;

    @Transactional
    public ReportDto createReportRequest(ReportDto dto, UUID generatedById) {
        Report report = new Report();
        report.setTitle(dto.getTitle());
        report.setDescription(dto.getDescription());
        report.setType(dto.getType());
        report.setDateFrom(dto.getDateFrom());
        report.setDateTo(dto.getDateTo());
        report.setFilters(dto.getFilters());
        report.setStatus(ReportStatus.PENDING);
        report.setGeneratedBy(userService.getUserEntity(generatedById));

        Report saved = reportRepository.save(report);
        generateReportAsync(saved.getId());
        return mapToDto(saved);
    }

    @Async
    @Transactional
    public void generateReportAsync(UUID reportId) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("Report not found"));

        Map<String, Object> data = new HashMap<>();

        if (report.getType() == ReportType.LIVESTOCK) {
            data.put("totalAnimals", animalRepository.count());
            data.put("sickCount", animalRepository.countSickAnimals());
        } else if (report.getType() == ReportType.FINANCIAL) {
            data.put("totalIncome", financialRecordRepository.sumIncomeBetween(report.getDateFrom(), report.getDateTo()));
            data.put("totalExpense", financialRecordRepository.sumExpenseBetween(report.getDateFrom(), report.getDateTo()));
        }
        // Add logic for other report types

        report.setData(data);
        report.setStatus(ReportStatus.COMPLETED);
        reportRepository.save(report);
    }

    @Transactional(readOnly = true)
    public ReportDto getReportById(UUID id) {
        return mapToDto(reportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Report not found")));
    }

    @Transactional(readOnly = true)
    public List<ReportDto> getAllReports() {
        return reportRepository.findAll().stream().map(this::mapToDto).collect(Collectors.toList());
    }

    private ReportDto mapToDto(Report r) {
        ReportDto dto = new ReportDto();
        dto.setId(r.getId());
        dto.setTitle(r.getTitle());
        dto.setDescription(r.getDescription());
        dto.setType(r.getType());
        dto.setDateFrom(r.getDateFrom());
        dto.setDateTo(r.getDateTo());
        dto.setFilters(r.getFilters());
        dto.setData(r.getData());
        dto.setStatus(r.getStatus());
        dto.setGeneratedById(r.getGeneratedBy().getId());
        dto.setCreatedAt(r.getCreatedAt());
        dto.setUpdatedAt(r.getUpdatedAt());
        return dto;
    }
}