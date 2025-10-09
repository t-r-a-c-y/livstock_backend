package com.livestock.backend.controller;

import com.livestock.backend.dto.SettingsDTO;
import com.livestock.backend.dto.ApiResponse;
import com.livestock.backend.service.SystemSettingsService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/settings")
public class SystemSettingsController {
    @Autowired
    private SystemSettingsService systemSettingsService;

    @GetMapping
    public ApiResponse<SettingsDTO> get() {
        return new ApiResponse<>(systemSettingsService.getSettings(), null);
    }

    @PutMapping
    public ApiResponse<Void> update(@Valid @RequestBody SettingsDTO dto) {
        systemSettingsService.update(dto);
        return new ApiResponse<>(null, null);
    }
}
