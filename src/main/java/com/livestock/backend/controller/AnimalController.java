package com.livestock.backend.controller;

import com.livestock.backend.dto.AnimalDTO;
import com.livestock.backend.dto.AnimalStatsDTO;
import com.livestock.backend.dto.AnimalWithOwnerDTO;
import com.livestock.backend.dto.ApiResponse;
import com.livestock.backend.service.AnimalService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/animals")
public class AnimalController {
    @Autowired
    private AnimalService animalService;

    @GetMapping
    public ApiResponse<Page<AnimalDTO>> list(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) UUID ownerId,
            Pageable pageable) {
        return new ApiResponse<>(animalService.getAll(type, status, ownerId, pageable), null);
    }

    @GetMapping("/{id}")
    public ApiResponse<AnimalWithOwnerDTO> get(@PathVariable UUID id) {
        return new ApiResponse<>(animalService.getById(id), null);
    }

    @PostMapping
    public ApiResponse<AnimalDTO> create(@Valid @RequestBody AnimalDTO dto) {
        return new ApiResponse<>(animalService.create(dto), null);
    }

    @PutMapping("/{id}")
    public ApiResponse<AnimalDTO> update(@PathVariable UUID id, @Valid @RequestBody AnimalDTO dto) {
        return new ApiResponse<>(animalService.update(id, dto), null);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable UUID id) {
        animalService.softDelete(id);
        return new ApiResponse<>(null, null);
    }

    @GetMapping("/stats")
    public ApiResponse<AnimalStatsDTO> stats() {
        return new ApiResponse<>(animalService.getStats(), null);
    }
}