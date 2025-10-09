package com.livestock.backend.controller;

import com.livestock.backend.dto.ActivityDTO;
import com.livestock.backend.dto.ApiResponse;
import com.livestock.backend.service.ActivityService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/activities")
public class ActivityController {
    @Autowired
    private ActivityService activityService;

    @GetMapping
    public ApiResponse<Page<ActivityDTO>> list(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) LocalDateTime startDate,
            @RequestParam(required = false) LocalDateTime endDate,
            @RequestParam(required = false) UUID animalId,
            Pageable pageable) {
        return new ApiResponse<>(activityService.getAll(type, startDate, endDate, animalId, pageable), null);
    }

    @GetMapping("/{id}")
    public ApiResponse<ActivityDTO> get(@PathVariable UUID id) {
        return new ApiResponse<>(activityService.getById(id), null);
    }

    @PostMapping
    public ApiResponse<ActivityDTO> create(@Valid @RequestBody ActivityDTO dto) {
        return new ApiResponse<>(activityService.create(dto), null);
    }

    @PutMapping("/{id}")
    public ApiResponse<ActivityDTO> update(@PathVariable UUID id, @Valid @RequestBody ActivityDTO dto) {
        return new ApiResponse<>(activityService.update(id, dto), null);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable UUID id) {
        activityService.softDelete(id);
        return new ApiResponse<>(null, null);
    }
}