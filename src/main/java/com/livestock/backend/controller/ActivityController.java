package com.livestock.backend.controller;

import com.livestock.backend.dto.ApiResponse;
import com.livestock.backend.dto.ActivityDTO;
import com.livestock.backend.dto.ActivityCreateDTO;
import com.livestock.backend.dto.ActivityUpdateDTO;
import com.livestock.backend.service.ActivityService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/api/activities")
@RequiredArgsConstructor
public class ActivityController {
    private final ActivityService activityService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<ActivityDTO>>> getAll(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            @RequestParam(required = false) UUID animalId,
            Pageable pageable) {
        ApiResponse<Page<ActivityDTO>> response = new ApiResponse<>();
        response.setData(activityService.getAllActivities(type, startDate, endDate, animalId, pageable));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ActivityDTO>> getById(@PathVariable UUID id) {
        ApiResponse<ActivityDTO> response = new ApiResponse<>();
        response.setData(activityService.getActivityById(id));
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ActivityDTO>> create(@Valid @RequestBody ActivityCreateDTO dto) {
        ApiResponse<ActivityDTO> response = new ApiResponse<>();
        response.setData(activityService.createActivity(dto));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ActivityDTO>> update(@PathVariable UUID id, @Valid @RequestBody ActivityUpdateDTO dto) {
        ApiResponse<ActivityDTO> response = new ApiResponse<>();
        response.setData(activityService.updateActivity(id, dto));
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        activityService.softDeleteActivity(id);
        return ResponseEntity.noContent().build();
    }
}