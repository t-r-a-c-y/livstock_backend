package com.livestock.backend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.livestock.backend.dto.request.ReportGenerateRequest;
import com.livestock.backend.dto.response.ReportResponse;
import com.livestock.backend.exception.ResourceNotFoundException;
import com.livestock.backend.model.Report;
import com.livestock.backend.repository.ReportRepository;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Map;

@Service
public class ReportService {

    private final ReportRepository reportRepository;
    private final ReportMapper reportMapper;
    private final AnimalService animalService;
    private final FinancialService financialService;
    private final AuthService authService;
    private final ObjectMapper objectMapper;

    public ReportService(ReportRepository reportRepository, ReportMapper reportMapper, AnimalService animalService, FinancialService financialService, AuthService authService, ObjectMapper objectMapper) {
        this.reportRepository = reportRepository;
        this.reportMapper = reportMapper;
        this.animalService = animalService;
        this.financialService = financialService;
        this.authService = authService;
        this.objectMapper = objectMapper;
    }

    public Page<ReportResponse> getAllReports(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return reportRepository.findAll(pageable).map(reportMapper::toResponse);
    }

    public ReportResponse generateReport(ReportGenerateRequest request) {
        Report report = reportMapper.toEntity(request);
        report.setGeneratedBy(authService.getCurrentUser());
        report.setCreatedAt(new Date());
        report = reportRepository.save(report);
        Map<String, Object> data = switch (request.getType()) {
            case "Livestock" -> animalService.getAnimalStats();
            case "Financial" -> financialService.getFinancialStats(request.getDateFrom(), request.getDateTo());
            default -> new java.util.HashMap<>();
        };
        try {
            report.setData(objectMapper.writeValueAsString(data));
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize report data", e);
        }
        report.setStatus("Completed");
        report = reportRepository.save(report);
        return reportMapper.toResponse(report);
    }

    public ReportResponse getReportById(java.util.UUID id) {
        Report report = reportRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Report not found"));
        return reportMapper.toResponse(report);
    }

    public ByteArrayInputStream downloadReport(java.util.UUID id, String format) throws IOException {
        Report report = reportRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Report not found"));
        return switch (format.toLowerCase()) {
            case "pdf" -> generatePdf(report);
            case "excel" -> generateExcel(report);
            default -> throw new IllegalArgumentException("Invalid format");
        };
    }

    private ByteArrayInputStream generatePdf(Report report) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(out);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);
        document.add(new Paragraph("Report: " + report.getTitle()));
        document.add(new Paragraph("Type: " + report.getType()));
        document.add(new Paragraph("Generated on: " + report.getCreatedAt()));
        document.add(new Paragraph("Data: " + report.getData()));
        document.close();
        return new ByteArrayInputStream(out.toByteArray());
    }

    private ByteArrayInputStream generateExcel(Report report) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet(report.getType() + " Report");
        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("Title");
        header.createCell(1).setCellValue("Type");
        header.createCell(2).setCellValue("Data");
        Row row = sheet.createRow(1);
        row.createCell(0).setCellValue(report.getTitle());
        row.createCell(1).setCellValue(report.getType());
        row.createCell(2).setCellValue(report.getData());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();
        return new ByteArrayInputStream(out.toByteArray());
    }
}