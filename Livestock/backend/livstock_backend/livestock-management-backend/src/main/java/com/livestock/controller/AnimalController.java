package com.livestock.controller;

import com.livestock.dto.request.AnimalRequest;
import com.livestock.dto.response.AnimalResponse;
import com.livestock.dto.response.ApiResponse;
import com.livestock.service.AnimalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/animals")
@RequiredArgsConstructor
public class AnimalController {

    private final AnimalService animalService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<AnimalResponse>>> getAllAnimals(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) UUID ownerId,
            @RequestParam(required = false) String search) {
        List<AnimalResponse> animals = animalService.getAllAnimals(status, type, ownerId, search);
        return ResponseEntity.ok(ApiResponse.success(animals));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<AnimalResponse>> createAnimal(
            @Valid @RequestBody AnimalRequest request,
            @RequestParam UUID ownerId) {  // ← Added ownerId parameter
        AnimalResponse animal = animalService.createAnimal(request, ownerId);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(animal));
    }

    @PostMapping("/{id}/upload-photo")
    public ResponseEntity<ApiResponse<AnimalResponse>> uploadPhoto(
            @PathVariable UUID id,
            @RequestParam("photo") MultipartFile photo) {
        AnimalResponse updated = animalService.uploadAnimalPhoto(id, photo);
        return ResponseEntity.ok(ApiResponse.success(updated));
    }

    // Add other endpoints as needed (getById, update, delete)...
}