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

    @GetMapping("/{key}")
    public ApiResponse<SettingsDTO> get(@PathVariable String key) {
        return new ApiResponse<>(systemSettingsService.getByKey(key), null);
    }

    @PutMapping("/{key}")
    public ApiResponse<SettingsDTO> update(@PathVariable String key, @Valid @RequestBody SettingsDTO dto) {
        return new ApiResponse<>(systemSettingsService.update(key, dto), null);
    }
}