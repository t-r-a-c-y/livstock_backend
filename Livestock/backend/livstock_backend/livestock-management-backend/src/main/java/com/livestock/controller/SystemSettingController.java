package com.livestock.controller;

import com.livestock.dto.ApiResponse;
import com.livestock.dto.SystemSettingDto;
import com.livestock.service.SystemSettingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/settings")
@RequiredArgsConstructor
public class SystemSettingController {

    private final SystemSettingService systemSettingService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<SystemSettingDto>> getSettings() {
        SystemSettingDto settings = systemSettingService.getSettings();
        return ResponseEntity.ok(ApiResponse.success(settings));
    }

    @PutMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<SystemSettingDto>> updateSettings(
            @Valid @RequestBody SystemSettingDto dto) {

        SystemSettingDto updated = systemSettingService.updateSettings(dto);
        return ResponseEntity.ok(ApiResponse.success(updated, "Settings updated"));
    }
}