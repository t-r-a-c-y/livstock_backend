// src/main/java/com/livestock/controller/ActivityController.java
package com.livestock.controller;

import com.livestock.dto.request.ActivityRequest;
import com.livestock.dto.response.ActivityResponse;
import com.livestock.dto.response.ApiResponse;
import com.livestock.service.ActivityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/activities")
public class ActivityController {

    private final ActivityService activityService;

    // Explicit constructor to avoid Lombok issues
    public ActivityController(ActivityService activityService) {
        this.activityService = activityService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ActivityResponse>>> getAllActivities(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) UUID animalId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {

        List<ActivityResponse> activities = activityService.getAllActivities(type, animalId, from, to);
        return ResponseEntity.ok(ApiResponse.success(activities));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ActivityResponse>> createActivity(@Valid @RequestBody ActivityRequest request) {
        ActivityResponse activity = activityService.createActivity(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(activity));
    }
}