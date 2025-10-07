package com.livestock.backend.service;


import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.livestock.backend.dto.request.ReportGenerateRequest;
import com.livestock.backend.dto.response.ReportResponse;
import com.livestock.backend.exception.ResourceNotFoundException;
import com.livestock.backend.model.Report;
import com.livestock.backend.repository.ReportRepository;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class ReportService {

    private final ReportRepository reportRepository;
    private final ReportMapper reportMapper;
    private final AnimalService animalService;
    private final FinancialService financialService;
    // Add other services for different report types

    public ReportService(ReportRepository reportRepository, ReportMapper reportMapper, AnimalService animalService, FinancialService financialService) {
        this.reportRepository = reportRepository;
        this.reportMapper = reportMapper;
        this.animalService = animalService;
        this.financialService = financialService;
    }

    public Page<ReportResponse> getAllReports(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return reportRepository.findAll(pageable).map(reportMapper::toResponse);
    }

    public ReportResponse generateReport(ReportGenerateRequest request) {
        Report report = reportMapper.toEntity(request);
        report.setGeneratedBy(authService.getCurrentUser()); // Assume authService injected
        report.setCreatedAt(new Date());
        report = reportRepository.save(report);
        // Generate data based on type
        Map<String, Object> data = new HashMap<>();
        if ("Livestock".equals(request.getType())) {
            data = animalService.getAnimalStats(); // Customize with filters
        } else if ("Financial".equals(request.getType())) {
            data = financialService.getFinancialStats(request.getDateFrom(), request.getDateTo());
        }
        // Set data as JSON string
        report.setData(new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(data));
        report.setStatus("Completed");
        report = reportRepository.save(report);
        return reportMapper.toResponse(report);
    }

    public ReportResponse getReportById(UUID id) {
        Report report = reportRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Report not found"));
        return reportMapper.toResponse(report);
    }

    public ByteArrayInputStream downloadReport(UUID id, String format) throws IOException {
        Report report = reportRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Report not found"));
        if ("pdf".equalsIgnoreCase(format)) {
            return generatePdf(report);
        } else if ("excel".equalsIgnoreCase(format)) {
            return generateExcel(report);
        }
        throw new IllegalArgumentException("Invalid format");
    }

    private ByteArrayInputStream generatePdf(Report report) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(out);
        PdfDocument pdf = new PdfDocument(writer);
        // Add content using iText
        // Example: new Paragraph(report.getTitle()).addTo(pdf);
        pdf.close();
        return new ByteArrayInputStream(out.toByteArray());
    }

    private ByteArrayInputStream generateExcel(Report report) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        // Add sheets and data
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();
        return new ByteArrayInputStream(out.toByteArray());
    }
}