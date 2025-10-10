package com.livestock.backend.service;

import com.livestock.backend.dto.SystemSettingsDTO;
import com.livestock.backend.model.SystemSettings;
import com.livestock.backend.repository.SystemSettingsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class SystemSettingsService {
    private static final Logger logger = LoggerFactory.getLogger(SystemSettingsService.class);

    @Autowired
    private SystemSettingsRepository systemSettingsRepository;

    @Transactional(readOnly = true)
    public SystemSettingsDTO getSettings() {
        logger.info("Fetching system settings");
        SystemSettings settings = systemSettingsRepository.findAll().stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("System settings not found"));
        return toDTO(settings);
    }

    @Transactional
    public SystemSettingsDTO update(SystemSettingsDTO dto) {
        logger.info("Updating system settings");
        SystemSettings settings = systemSettingsRepository.findAll().stream()
                .findFirst()
                .orElse(new SystemSettings());
        updateEntityFromDTO(settings, dto);
        settings.setUpdatedAt(LocalDateTime.now());
        settings = systemSettingsRepository.save(settings);
        return toDTO(settings);
    }

    private SystemSettingsDTO toDTO(SystemSettings settings) {
        SystemSettingsDTO dto = new SystemSettingsDTO();
        dto.setId(settings.getId());
        dto.setOrganizationName(settings.getOrganizationName());
        dto.setCurrency(settings.getCurrency());
        dto.setTimezone(settings.getTimezone());
        dto.setLanguage(settings.getLanguage());
        dto.setDateFormat(settings.getDateFormat());
        dto.setTheme(settings.getTheme());
        dto.setAutoBackup(settings.isAutoBackup());
        dto.setDataRetention(settings.getDataRetention());
        dto.setEmailReports(settings.isEmailReports());
        dto.setMobileNotifications(settings.isMobileNotifications());
        dto.setNotificationEmail(settings.getNotificationEmail());
        dto.setUpdatedAt(settings.getUpdatedAt());
        return dto;
    }

    private void updateEntityFromDTO(SystemSettings settings, SystemSettingsDTO dto) {
        settings.setOrganizationName(dto.getOrganizationName());
        settings.setCurrency(dto.getCurrency());
        settings.setTimezone(dto.getTimezone());
        settings.setLanguage(dto.getLanguage());
        settings.setDateFormat(dto.getDateFormat());
        settings.setTheme(dto.getTheme());
        settings.setAutoBackup(dto.isAutoBackup());
        settings.setDataRetention(dto.getDataRetention());
        settings.setEmailReports(dto.isEmailReports());
        settings.setMobileNotifications(dto.isMobileNotifications());
        settings.setNotificationEmail(dto.getNotificationEmail());
    }
}