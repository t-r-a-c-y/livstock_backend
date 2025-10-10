package com.livestock.backend.controller;

import com.livestock.backend.dto.ActivityDTO;
import com.livestock.backend.service.ActivityService;
import com.livestock.backend.util.ResponseWrapper;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/api/activities")
public class ActivityController {
    private static final Logger logger = LoggerFactory.getLogger(ActivityController.class);

    @Autowired
    private ActivityService activityService;

    @GetMapping
    public ResponseEntity<ResponseWrapper<Page<ActivityDTO>>> getAll(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo,
            @RequestParam(required = false) UUID animalId,
            Pageable pageable) {
        logger.info("Received request to fetch activities with type: {}, dateFrom: {}, dateTo: {}, animalId: {}", type, dateFrom, dateTo, animalId);
        Page<ActivityDTO> activities = activityService.getAll(type, dateFrom, dateTo, animalId, pageable);
        return ResponseEntity.ok(new ResponseWrapper<>(activities, null));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseWrapper<ActivityDTO>> getById(@PathVariable UUID id) {
        logger.info("Received request to fetch activity with id: {}", id);
        try {
            ActivityDTO activity = activityService.getById(id);
            return ResponseEntity.ok(new ResponseWrapper<>(activity, null));
        } catch (RuntimeException e) {
            logger.error("Error fetching activity with id: {}", id, e);
            return ResponseEntity.badRequest().body(new ResponseWrapper<>(null, new ResponseWrapper.ErrorMessage(e.getMessage())));
        }
    }

    @PostMapping
    public ResponseEntity<ResponseWrapper<ActivityDTO>> create(@Valid @RequestBody ActivityDTO activityDTO) {
        logger.info("Received request to create activity with type: {}", activityDTO.getType());
        try {
            ActivityDTO createdActivity = activityService.create(activityDTO);
            return ResponseEntity.ok(new ResponseWrapper<>(createdActivity, null));
        } catch (RuntimeException e) {
            logger.error("Error creating activity: {}", e.getMessage());
            return ResponseEntity.badRequest().body(new ResponseWrapper<>(null, new ResponseWrapper.ErrorMessage(e.getMessage())));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseWrapper<ActivityDTO>> update(@PathVariable UUID id, @Valid @RequestBody ActivityDTO activityDTO) {
        logger.info("Received request to update activity with id: {}", id);
        try {
            ActivityDTO updatedActivity = activityService.update(id, activityDTO);
            return ResponseEntity.ok(new ResponseWrapper<>(updatedActivity, null));
        } catch (RuntimeException e) {
            logger.error("Error updating activity with id: {}", id, e);
            return ResponseEntity.badRequest().body(new ResponseWrapper<>(null, new ResponseWrapper.ErrorMessage(e.getMessage())));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseWrapper<Void>> delete(@PathVariable UUID id) {
        logger.info("Received request to delete activity with id: {}", id);
        try {
            activityService.delete(id);
            return ResponseEntity.ok(new ResponseWrapper<>(null, null));
        } catch (RuntimeException e) {
            logger.error("Error deleting activity with id: {}", id, e);
            return ResponseEntity.badRequest().body(new ResponseWrapper<>(null, new ResponseWrapper.ErrorMessage(e.getMessage())));
        }
    }
}