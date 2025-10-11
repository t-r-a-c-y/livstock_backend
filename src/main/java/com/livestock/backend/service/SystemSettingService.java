package com.livestock.backend.service;

import com.livestock.backend.dto.SystemSettingDTO;
import com.livestock.backend.dto.SystemSettingUpdateDTO;
import com.livestock.backend.model.SystemSetting;
import com.livestock.backend.repository.SystemSettingRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SystemSettingService {
    private static final Logger logger = LoggerFactory.getLogger(SystemSettingService.class);
    private final SystemSettingRepository systemSettingRepository;
    private final ModelMapper modelMapper;

    public SystemSettingDTO getSystemSettings() {
        logger.info("Fetching system settings");
        SystemSetting settings = systemSettingRepository.findFirst()
                .orElseThrow(() -> new RuntimeException("System settings not found"));
        return modelMapper.map(settings, SystemSettingDTO.class);
    }

    @Transactional
    public SystemSettingDTO updateSystemSettings(SystemSettingUpdateDTO dto) {
        logger.info("Updating system settings");
        SystemSetting settings = systemSettingRepository.findFirst()
                .orElseGet(() -> {
                    SystemSetting newSettings = new SystemSetting();
                    newSettings.setId(UUID.randomUUID());
                    return newSettings;
                });
        modelMapper.map(dto, settings);
        settings.setUpdatedAt(LocalDateTime.now());
        settings = systemSettingRepository.save(settings);
        return modelMapper.map(settings, SystemSettingDTO.class);
    }
}