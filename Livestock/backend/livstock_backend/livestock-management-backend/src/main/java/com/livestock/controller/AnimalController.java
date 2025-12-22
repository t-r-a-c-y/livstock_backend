// src/main/java/com/livestock/controller/AnimalController.java
package com.livestock.controller;

import com.livestock.dto.request.AnimalRequest;
import com.livestock.dto.response.AnimalResponse;
import com.livestock.dto.response.ApiResponse;
import com.livestock.repository.AnimalRepository;
import com.livestock.service.AnimalService;
import com.livestock.service.PdfReportService;
import com.lowagie.text.DocumentException;
import jakarta.validation.Valid;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/animals")
public class AnimalController {

    private final AnimalService animalService;
    private final AnimalRepository animalRepository;
    private final PdfReportService pdfReportService;

    public AnimalController(AnimalService animalService,
                            AnimalRepository animalRepository,
                            PdfReportService pdfReportService) {
        this.animalService = animalService;
        this.animalRepository = animalRepository;
        this.pdfReportService = pdfReportService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<AnimalResponse>>> getAllAnimals(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) UUID ownerId,
            @RequestParam(required = false) String search) {

        List<AnimalResponse> animals = animalService.getAllAnimals(status, type, ownerId, search);
        return ResponseEntity.ok(ApiResponse.success(animals));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AnimalResponse>> getAnimal(@PathVariable UUID id) {
        AnimalResponse animal = animalService.getAnimalById(id);
        return ResponseEntity.ok(ApiResponse.success(animal));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<AnimalResponse>> createAnimal(@Valid @RequestBody AnimalRequest request) {
        AnimalResponse animal = animalService.createAnimal(request);
        return ResponseEntity.status(201).body(ApiResponse.success(animal));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<AnimalResponse>> updateAnimal(
            @PathVariable UUID id, @Valid @RequestBody AnimalRequest request) {
        AnimalResponse animal = animalService.updateAnimal(id, request);
        return ResponseEntity.ok(ApiResponse.success(animal));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteAnimal(@PathVariable UUID id) {
        animalService.deleteAnimal(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @PostMapping("/{id}/upload-photo")
    public ResponseEntity<ApiResponse<AnimalResponse>> uploadPhoto(
            @PathVariable UUID id,
            @RequestParam("photo") MultipartFile photo) {

        AnimalResponse updatedAnimal = animalService.uploadAnimalPhoto(id, photo);
        return ResponseEntity.ok(ApiResponse.success(updatedAnimal));
    }

    @GetMapping("/reports/animals/pdf")
    public ResponseEntity<InputStreamResource> exportAnimalsPdf() throws DocumentException {
        List<Object[]> animals = animalRepository.findAllForReport();

        ByteArrayInputStream bis = pdfReportService.generateAnimalReport(animals);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=animal-report-" + LocalDate.now() + ".pdf");
        headers.add("Content-Type", "application/pdf");

        InputStreamResource resource = new InputStreamResource(bis);

        return ResponseEntity.ok()
                .headers(headers)
                .body(resource);
    }
}