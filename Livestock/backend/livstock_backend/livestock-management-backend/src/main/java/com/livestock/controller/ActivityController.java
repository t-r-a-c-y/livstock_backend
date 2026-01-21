package com.livestock.controller;

import com.livestock.dto.ActivityDto;
import com.livestock.dto.ApiResponse;
import com.livestock.service.ActivityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/activities")
@RequiredArgsConstructor
public class ActivityController {

    private final ActivityService activityService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','STAFF','VIEWER')")
    public ResponseEntity<ApiResponse<List<ActivityDto>>> getAllActivities() {
        List<ActivityDto> activities = activityService.getAllActivities();
        return ResponseEntity.ok(ApiResponse.success(activities, "Activities retrieved"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','STAFF','VIEWER')")
    public ResponseEntity<ApiResponse<ActivityDto>> getActivityById(@PathVariable UUID id) {
        ActivityDto dto = activityService.getActivityById(id);
        return ResponseEntity.ok(ApiResponse.success(dto));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','STAFF')")
    public ResponseEntity<ApiResponse<ActivityDto>> createActivity(
            @Valid @RequestBody ActivityDto dto,
            Authentication authentication) {

        UUID currentUserId = UUID.fromString(authentication.getName());
        ActivityDto created = activityService.createActivity(dto, currentUserId);
        return ResponseEntity.ok(ApiResponse.success(created, "Activity created successfully"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','STAFF')")
    public ResponseEntity<ApiResponse<ActivityDto>> updateActivity(
            @PathVariable UUID id,
            @Valid @RequestBody ActivityDto dto,
            Authentication authentication) {

        UUID currentUserId = UUID.fromString(authentication.getName());
        ActivityDto updated = activityService.updateActivity(id, dto, currentUserId);
        return ResponseEntity.ok(ApiResponse.success(updated, "Activity updated successfully"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<ApiResponse<Void>> deleteActivity(@PathVariable UUID id) {
        activityService.deleteActivity(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Activity deleted successfully"));
    }

    @GetMapping("/animal/{animalId}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','STAFF','VIEWER')")
    public ResponseEntity<ApiResponse<List<ActivityDto>>> getActivitiesByAnimal(@PathVariable UUID animalId) {
        List<ActivityDto> activities = activityService.getActivitiesByAnimal(animalId);
        return ResponseEntity.ok(ApiResponse.success(activities));
    }

    @GetMapping("/date-range")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','STAFF','VIEWER')")
    public ResponseEntity<ApiResponse<List<ActivityDto>>> getActivitiesByDateRange(
            @RequestParam LocalDate start,
            @RequestParam LocalDate end) {
        List<ActivityDto> activities = activityService.getActivitiesByDateRange(start, end);
        return ResponseEntity.ok(ApiResponse.success(activities));
    }
}