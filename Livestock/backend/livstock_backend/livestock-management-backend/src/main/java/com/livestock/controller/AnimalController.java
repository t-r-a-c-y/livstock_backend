// src/main/java/com/livestock/controller/AnimalController.java
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

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AnimalResponse>> getAnimal(@PathVariable UUID id) {
        AnimalResponse animal = animalService.getAnimalById(id);
        return ResponseEntity.ok(ApiResponse.success(animal));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<AnimalResponse>> createAnimal(@Valid @RequestBody AnimalRequest request) {
        AnimalResponse animal = animalService.createAnimal(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(animal));
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
}