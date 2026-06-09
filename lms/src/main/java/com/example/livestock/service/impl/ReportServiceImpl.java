package com.example.livestock.service.impl;

import com.example.livestock.dto.ReportLogResponse;
import com.example.livestock.dto.ReportRequest;
import com.example.livestock.entity.Animal;
import com.example.livestock.entity.ReportLog;
import com.example.livestock.entity.User;
import com.example.livestock.enums.ExportFormat;
import com.example.livestock.enums.NotificationType;
import com.example.livestock.enums.Role;
import com.example.livestock.exception.ForbiddenException;
import com.example.livestock.mapper.DtoMapper;
import com.example.livestock.report.ReportData;
import com.example.livestock.report.ReportRow;
import com.example.livestock.repository.*;
import com.example.livestock.security.CurrentUserService;
import com.example.livestock.service.AuditLogService;
import com.example.livestock.service.NotificationService;
import com.example.livestock.service.ReportService;
import com.lowagie.text.Document;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ReportServiceImpl implements ReportService {
    private final AnimalRepository animalRepository;
    private final BreedingRecordRepository breedingRecordRepository;
    private final HealthRecordRepository healthRecordRepository;
    private final VaccinationRecordRepository vaccinationRecordRepository;
    private final MessageRepository messageRepository;
    private final NotificationRepository notificationRepository;
    private final ReportLogRepository reportLogRepository;
    private final CurrentUserService currentUserService;
    private final NotificationService notificationService;
    private final AuditLogService auditLogService;

    @Override
    public ResponseEntity<Resource> export(ReportRequest request) {
        User user = currentUserService.getCurrentUser();
        if (request.includeInactive() && user.getRole() != Role.ADMIN) {
            throw new ForbiddenException("Only admins can export inactive records");
        }
        ReportData data = data(request, user);
        byte[] bytes = request.exportFormat() == ExportFormat.PDF ? pdf(data, user) : csv(data);
        String extension = request.exportFormat().name().toLowerCase();
        String filename = request.reportType().toLowerCase().replace(" ", "-") + "-" + System.currentTimeMillis() + "." + extension;

        ReportLog log = new ReportLog();
        log.setReportName(data.title());
        log.setReportType(request.reportType());
        log.setExportFormat(request.exportFormat());
        log.setGeneratedBy(user);
        log.setGeneratedAt(LocalDateTime.now());
        log.setFilePath(filename);
        reportLogRepository.save(log);
        notificationService.notify(user, "Report generated", data.title() + " is ready for download.", NotificationType.REPORT_READY);
        auditLogService.record("GENERATE_REPORT", "ReportLog", log.getId(), user,
                data.title() + " generated as " + request.exportFormat());

        MediaType mediaType = request.exportFormat() == ExportFormat.PDF ? MediaType.APPLICATION_PDF : MediaType.parseMediaType("text/csv");
        return ResponseEntity.ok()
                .contentType(mediaType)
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment().filename(filename).build().toString())
                .body(new ByteArrayResource(bytes));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReportLogResponse> logs(Pageable pageable) {
        return reportLogRepository.findByActiveTrue(pageable).map(DtoMapper::toReportLog);
    }

    private ReportData data(ReportRequest request, User user) {
        String type = request.reportType().toUpperCase();
        if (type.contains("BREEDING")) {
            return new ReportData("Breeding Records", List.of("Cow", "Owner", "Mating Date", "Status"),
                    breedingRecordRepository.findByActiveTrue(Pageable.unpaged()).stream()
                            .filter(r -> user.getRole() == Role.ADMIN || r.getCow().getOwner().getUser().getId().equals(user.getId()))
                            .filter(r -> request.ownerId() == null || r.getCow().getOwner().getId().equals(request.ownerId()))
                            .filter(r -> inRange(r.getCreatedAt(), request))
                            .map(r -> new ReportRow(List.of(r.getCow().getTagNumber(), r.getCow().getOwner().getUser().getFullName(),
                                    String.valueOf(r.getMatingDate()), String.valueOf(r.getPregnancyStatus())))).toList());
        }
        if (type.contains("HEALTH")) {
            return new ReportData("Health Records", List.of("Animal", "Diagnosis", "Treatment", "Visit Date"),
                    healthRecordRepository.findByActiveTrue(Pageable.unpaged()).stream()
                            .filter(r -> user.getRole() == Role.ADMIN || r.getAnimal().getOwner().getUser().getId().equals(user.getId()))
                            .filter(r -> request.ownerId() == null || r.getAnimal().getOwner().getId().equals(request.ownerId()))
                            .filter(r -> inRange(r.getCreatedAt(), request))
                            .map(r -> new ReportRow(List.of(r.getAnimal().getTagNumber(), r.getDiagnosis(), value(r.getTreatment()),
                                    String.valueOf(r.getVisitDate())))).toList());
        }
        if (type.contains("VACCINATION")) {
            return new ReportData("Vaccination Records", List.of("Animal", "Vaccine", "Date", "Next Due"),
                    vaccinationRecordRepository.findByActiveTrue(Pageable.unpaged()).stream()
                            .filter(r -> user.getRole() == Role.ADMIN || r.getAnimal().getOwner().getUser().getId().equals(user.getId()))
                            .filter(r -> request.ownerId() == null || r.getAnimal().getOwner().getId().equals(request.ownerId()))
                            .filter(r -> inRange(r.getCreatedAt(), request))
                            .map(r -> new ReportRow(List.of(r.getAnimal().getTagNumber(), r.getVaccineName(),
                                    String.valueOf(r.getVaccinationDate()), value(r.getNextDueDate())))).toList());
        }
        if (type.contains("MESSAGE")) {
            return new ReportData("Messages", List.of("Sender", "Receiver", "Subject", "Status"),
                    messageRepository.findByActiveTrue(Pageable.unpaged()).stream()
                            .filter(m -> user.getRole() == Role.ADMIN || m.getSender().getId().equals(user.getId()) || m.getReceiver().getId().equals(user.getId()))
                            .filter(m -> inRange(m.getCreatedAt(), request))
                            .map(m -> new ReportRow(List.of(m.getSender().getFullName(), m.getReceiver().getFullName(),
                                    m.getSubject(), String.valueOf(m.getMessageStatus())))).toList());
        }
        if (type.contains("NOTIFICATION")) {
            return new ReportData("Notifications", List.of("Title", "Type", "Read", "Created"),
                    notificationRepository.findByUserIdAndActiveTrue(user.getId(), Pageable.unpaged()).stream()
                            .map(n -> new ReportRow(List.of(n.getTitle(), String.valueOf(n.getNotificationType()),
                                    String.valueOf(n.isReadStatus()), String.valueOf(n.getCreatedAt())))).toList());
        }
        List<Animal> animals = (request.includeInactive()
                ? animalRepository.findAll(Pageable.unpaged())
                : animalRepository.findByActiveTrue(Pageable.unpaged())).stream()
                .filter(a -> request.ownerId() == null || a.getOwner().getId().equals(request.ownerId()))
                .filter(a -> request.animalType() == null || a.getAnimalType() == request.animalType())
                .filter(a -> request.status() == null || a.getAnimalStatus() == request.status())
                .filter(a -> user.getRole() == Role.ADMIN || a.getOwner().getUser().getId().equals(user.getId()))
                .filter(a -> inRange(a.getCreatedAt(), request))
                .toList();
        return new ReportData("Livestock Report", List.of("Tag", "Owner", "Type", "Breed", "Gender", "Status"),
                animals.stream().map(a -> new ReportRow(List.of(a.getTagNumber(), a.getOwner().getUser().getFullName(),
                        String.valueOf(a.getAnimalType()), value(a.getBreed()), String.valueOf(a.getGender()),
                        String.valueOf(a.getAnimalStatus())))).toList());
    }

    private byte[] csv(ReportData data) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            try (CSVPrinter printer = new CSVPrinter(new OutputStreamWriter(out, StandardCharsets.UTF_8),
                    CSVFormat.EXCEL.builder().setHeader(data.headers().toArray(String[]::new)).build())) {
                for (ReportRow row : data.rows()) {
                    printer.printRecord(row.cells());
                }
            }
            return out.toByteArray();
        } catch (Exception ex) {
            throw new IllegalStateException("Could not generate CSV report", ex);
        }
    }

    private byte[] pdf(ReportData data, User user) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Document document = new Document(PageSize.A4.rotate());
            PdfWriter.getInstance(document, out);
            document.open();
            document.add(new Paragraph(data.title()));
            document.add(new Paragraph("Generated: " + LocalDateTime.now()));
            document.add(new Paragraph("Generated by: " + user.getFullName()));
            PdfPTable table = new PdfPTable(data.headers().size());
            data.headers().forEach(header -> table.addCell(new PdfPCell(new Phrase(header))));
            data.rows().forEach(row -> row.cells().forEach(table::addCell));
            document.add(table);
            document.add(new Paragraph("Page 1"));
            document.close();
            return out.toByteArray();
        } catch (Exception ex) {
            throw new IllegalStateException("Could not generate PDF report", ex);
        }
    }

    private String value(Object value) {
        return value == null ? "" : String.valueOf(value);
    }

    private boolean inRange(LocalDateTime value, ReportRequest request) {
        if (request.fromDate() != null && value.toLocalDate().isBefore(request.fromDate())) {
            return false;
        }
        return request.toDate() == null || !value.toLocalDate().isAfter(request.toDate());
    }
}
