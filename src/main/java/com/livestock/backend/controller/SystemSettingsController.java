package com.livestock.backend.controller;

import com.livestock.backend.dto.SystemSettingsDTO;
import com.livestock.backend.service.SystemSettingsService;
import com.livestock.backend.util.ResponseWrapper;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/settings")
public class SystemSettingsController {
    private static final Logger logger = LoggerFactory.getLogger(SystemSettingsController.class);

    @Autowired
    private SystemSettingsService systemSettingsService;

    @GetMapping
    public ResponseEntity<ResponseWrapper<SystemSettingsDTO>> getSettings() {
        logger.info("Received request to fetch system settings");
        try {
            SystemSettingsDTO settings = systemSettingsService.getSettings();
            return ResponseEntity.ok(new ResponseWrapper<>(settings, null));
        } catch (RuntimeException e) {
            logger.error("Error fetching system settings: {}", e.getMessage());
            return ResponseEntity.badRequest().body(new ResponseWrapper<>(null, new ResponseWrapper.ErrorMessage(e.getMessage())));
        }
    }

    @PutMapping
    public ResponseEntity<ResponseWrapper<SystemSettingsDTO>> update(@Valid @RequestBody SystemSettingsDTO settingsDTO) {
        logger.info("Received request to update system settings");
        try {
            SystemSettingsDTO updatedSettings = systemSettingsService.update(settingsDTO);
            return ResponseEntity.ok(new ResponseWrapper<>(updatedSettings, null));
        } catch (RuntimeException e) {
            logger.error("Error updating system settings: {}", e.getMessage());
            return ResponseEntity.badRequest().body(new ResponseWrapper<>(null, new ResponseWrapper.ErrorMessage(e.getMessage())));
        }
    }
}