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
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {
    private final OwnerService ownerService;
    private final AnimalService animalService;
    private final BreedingRecordService breedingRecordService;
    private final HealthRecordService healthRecordService;
    private final VaccinationRecordService vaccinationRecordService;
    private final MessageService messageService;
    private final ReportService reportService;

    @PostMapping("/owners")
    public ResponseEntity<OwnerResponse> createOwner(@Valid @RequestBody OwnerRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ownerService.create(request));
    }

    @GetMapping("/owners")
    public Page<OwnerResponse> owners(Pageable pageable) {
        return ownerService.findAll(pageable);
    }

    @PutMapping("/owners/{id}")
    public OwnerResponse updateOwner(@PathVariable Long id, @Valid @RequestBody OwnerRequest request) {
        return ownerService.update(id, request);
    }

    @DeleteMapping("/owners/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deactivateOwner(@PathVariable Long id) {
        ownerService.deactivate(id);
    }

    @PostMapping("/animals")
    public ResponseEntity<AnimalResponse> createAnimal(@Valid @RequestBody AnimalRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(animalService.create(request));
    }

    @GetMapping("/animals")
    public Page<AnimalResponse> animals(@RequestParam(defaultValue = "false") boolean includeInactive, Pageable pageable) {
        return animalService.findAll(includeInactive, pageable);
    }

    @PutMapping("/animals/{id}")
    public AnimalResponse updateAnimal(@PathVariable Long id, @Valid @RequestBody AnimalRequest request) {
        return animalService.update(id, request);
    }

    @DeleteMapping("/animals/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deactivateAnimal(@PathVariable Long id) {
        animalService.deactivate(id);
    }

    @PostMapping("/breeding-records")
    public ResponseEntity<BreedingRecordResponse> addBreeding(@Valid @RequestBody BreedingRecordRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(breedingRecordService.create(request));
    }

    @PutMapping("/breeding-records/{id}")
    public BreedingRecordResponse updateBreeding(@PathVariable Long id, @Valid @RequestBody BreedingRecordRequest request) {
        return breedingRecordService.update(id, request);
    }

    @GetMapping("/breeding-records")
    public Page<BreedingRecordResponse> breedingRecords(Pageable pageable) {
        return breedingRecordService.findAll(pageable);
    }

    @PostMapping("/health-records")
    public ResponseEntity<HealthRecordResponse> addHealth(@Valid @RequestBody HealthRecordRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(healthRecordService.create(request));
    }

    @GetMapping("/health-records")
    public Page<HealthRecordResponse> healthRecords(Pageable pageable) {
        return healthRecordService.findAll(pageable);
    }

    @PostMapping("/vaccination-records")
    public ResponseEntity<VaccinationRecordResponse> addVaccination(@Valid @RequestBody VaccinationRecordRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(vaccinationRecordService.create(request));
    }

    @GetMapping("/vaccination-records")
    public Page<VaccinationRecordResponse> vaccinationRecords(Pageable pageable) {
        return vaccinationRecordService.findAll(pageable);
    }

    @GetMapping("/messages")
    public Page<MessageResponse> messages(Pageable pageable) {
        return messageService.findAll(pageable);
    }

    @PostMapping("/messages/reply")
    public MessageResponse reply(@Valid @RequestBody MessageReplyRequest request) {
        return messageService.reply(request);
    }

    @PostMapping("/reports/export")
    public ResponseEntity<Resource> export(@Valid @RequestBody ReportRequest request) {
        return reportService.export(request);
    }

    @GetMapping("/report-logs")
    public Page<ReportLogResponse> reportLogs(Pageable pageable) {
        return reportService.logs(pageable);
    }
}
