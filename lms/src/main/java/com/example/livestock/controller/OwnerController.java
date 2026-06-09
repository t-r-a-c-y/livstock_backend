package com.example.livestock.controller;

import com.example.livestock.dto.*;
import com.example.livestock.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/owner")
@RequiredArgsConstructor
public class OwnerController {
    private final OwnerService ownerService;
    private final AnimalService animalService;
    private final BreedingRecordService breedingRecordService;
    private final HealthRecordService healthRecordService;
    private final VaccinationRecordService vaccinationRecordService;
    private final MessageService messageService;
    private final NotificationService notificationService;
    private final ReportService reportService;

    @GetMapping("/profile")
    public OwnerResponse profile() {
        return ownerService.currentProfile();
    }

    @GetMapping("/animals")
    public Page<AnimalResponse> animals(Pageable pageable) {
        return animalService.findMine(pageable);
    }

    @GetMapping("/breeding-records")
    public Page<BreedingRecordResponse> breedingRecords(Pageable pageable) {
        return breedingRecordService.findMine(pageable);
    }

    @GetMapping("/health-records")
    public Page<HealthRecordResponse> healthRecords(Pageable pageable) {
        return healthRecordService.findMine(pageable);
    }

    @GetMapping("/vaccination-records")
    public Page<VaccinationRecordResponse> vaccinationRecords(Pageable pageable) {
        return vaccinationRecordService.findMine(pageable);
    }

    @PostMapping("/messages")
    public ResponseEntity<MessageResponse> sendMessage(@Valid @RequestBody MessageRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(messageService.sendToAdmin(request));
    }

    @GetMapping("/messages")
    public Page<MessageResponse> messages(Pageable pageable) {
        return messageService.findMine(pageable);
    }

    @GetMapping("/notifications")
    public Page<NotificationResponse> notifications(Pageable pageable) {
        return notificationService.findMine(pageable);
    }

    @PatchMapping("/notifications/{id}/read")
    public NotificationResponse markRead(@PathVariable Long id) {
        return notificationService.markRead(id);
    }

    @PostMapping("/reports/export")
    public ResponseEntity<Resource> export(@Valid @RequestBody ReportRequest request) {
        return reportService.export(request);
    }
}
