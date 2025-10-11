package com.livestock.backend.controller;

import com.livestock.backend.dto.ApiResponse;
import com.livestock.backend.dto.AnimalDTO;
import com.livestock.backend.dto.AnimalCreateDTO;
import com.livestock.backend.dto.AnimalUpdateDTO;
import com.livestock.backend.service.AnimalService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/animals")
@RequiredArgsConstructor
public class AnimalController {
    private final AnimalService animalService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<AnimalDTO>>> getAll(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) UUID ownerId,
            Pageable pageable) {
        ApiResponse<Page<AnimalDTO>> response = new ApiResponse<>();
        response.setData(animalService.getAllAnimals(type, status, ownerId, pageable));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AnimalDTO>> getById(@PathVariable UUID id) {
        ApiResponse<AnimalDTO> response = new ApiResponse<>();
        response.setData(animalService.getAnimalById(id));
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<AnimalDTO>> create(@Valid @RequestBody AnimalCreateDTO dto) {
        ApiResponse<AnimalDTO> response = new ApiResponse<>();
        response.setData(animalService.createAnimal(dto));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<AnimalDTO>> update(@PathVariable UUID id, @Valid @RequestBody AnimalUpdateDTO dto) {
        ApiResponse<AnimalDTO> response = new ApiResponse<>();
        response.setData(animalService.updateAnimal(id, dto));
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        animalService.softDeleteAnimal(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getStats() {
        ApiResponse<Map<String, Object>> response = new ApiResponse<>();
        response.setData(animalService.getAnimalStats());
        return ResponseEntity.ok(response);
    }
}