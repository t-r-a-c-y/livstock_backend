package com.livestock.backend.controller;

import com.livestock.backend.dto.ApiResponse;
import com.livestock.backend.dto.SystemSettingDTO;
import com.livestock.backend.dto.SystemSettingUpdateDTO;
import com.livestock.backend.service.SystemSettingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/settings")
@RequiredArgsConstructor
public class SystemSettingController {
    private final SystemSettingService systemSettingService;

    @GetMapping
    public ResponseEntity<ApiResponse<SystemSettingDTO>> getSettings() {
        ApiResponse<SystemSettingDTO> response = new ApiResponse<>();
        response.setData(systemSettingService.getSystemSettings());
        return ResponseEntity.ok(response);
    }

    @PutMapping
    public ResponseEntity<ApiResponse<SystemSettingDTO>> update(@Valid @RequestBody SystemSettingUpdateDTO dto) {
        ApiResponse<SystemSettingDTO> response = new ApiResponse<>();
        response.setData(systemSettingService.updateSystemSettings(dto));
        return ResponseEntity.ok(response);
    }
}