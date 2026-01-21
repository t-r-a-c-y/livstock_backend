package com.livestock.controller;

import com.livestock.dto.AnimalDto;
import com.livestock.dto.ApiResponse;
import com.livestock.entity.enums.AnimalType;
import com.livestock.service.AnimalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/animals")
@RequiredArgsConstructor
public class AnimalController {

    private final AnimalService animalService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','STAFF','VIEWER')")
    public ResponseEntity<ApiResponse<List<AnimalDto>>> getAllAnimals() {
        return ResponseEntity.ok(ApiResponse.success(animalService.getAllAnimals()));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','STAFF','VIEWER')")
    public ResponseEntity<ApiResponse<AnimalDto>> getAnimalById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(animalService.getAnimalById(id)));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','STAFF')")
    public ResponseEntity<ApiResponse<AnimalDto>> createAnimal(
            @Valid @RequestBody AnimalDto dto,
            Authentication authentication) {

        UUID currentUserId = UUID.fromString(authentication.getName());
        AnimalDto created = animalService.createAnimal(dto);
        return ResponseEntity.ok(ApiResponse.success(created, "Animal created"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','STAFF')")
    public ResponseEntity<ApiResponse<AnimalDto>> updateAnimal(
            @PathVariable UUID id,
            @Valid @RequestBody AnimalDto dto) {

        AnimalDto updated = animalService.updateAnimal(id, dto);
        return ResponseEntity.ok(ApiResponse.success(updated, "Animal updated"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<ApiResponse<Void>> deleteAnimal(@PathVariable UUID id) {
        animalService.deleteAnimal(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Animal deleted"));
    }

    @GetMapping("/owner/{ownerId}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','STAFF','VIEWER')")
    public ResponseEntity<ApiResponse<List<AnimalDto>>> getAnimalsByOwner(@PathVariable UUID ownerId) {
        return ResponseEntity.ok(ApiResponse.success(animalService.getAnimalsByOwner(ownerId)));
    }

    @GetMapping("/type/{type}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','STAFF','VIEWER')")
    public ResponseEntity<ApiResponse<List<AnimalDto>>> getAnimalsByType(
            @PathVariable AnimalType type) {

        List<AnimalDto> animals = animalService.getAnimalsByType(type);
        return ResponseEntity.ok(ApiResponse.success(animals, "Animals of type " + type + " retrieved"));
    }

    @ExceptionHandler(ConversionFailedException.class)
    public ResponseEntity<ApiResponse<Void>> handleInvalidAnimalType(ConversionFailedException ex) {
        String validTypes = String.join(", ",
                Arrays.stream(AnimalType.values())
                        .map(Enum::name)
                        .toArray(String[]::new)
        );

        return ResponseEntity.badRequest()
                .body(ApiResponse.error("Invalid animal type: '" + ex.getValue() +
                        "'. Valid values are: " + validTypes));
    }

    @GetMapping("/sick")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','STAFF')")
    public ResponseEntity<ApiResponse<List<AnimalDto>>> getSickAnimals() {
        return ResponseEntity.ok(ApiResponse.success(animalService.getSickAnimals()));
    }
}