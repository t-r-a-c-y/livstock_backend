package com.livestock.service;

import com.livestock.dto.SystemSettingDto;
import com.livestock.entity.SystemSetting;
import com.livestock.repository.SystemSettingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SystemSettingService {

    private final SystemSettingRepository systemSettingRepository;

    @Transactional
    public SystemSettingDto getSettings() {
        // Usually there's only one settings row
        return systemSettingRepository.findAll().stream()
                .findFirst()
                .map(this::mapToDto)
                .orElseGet(this::createDefaultSettings);
    }

    @Transactional
    public SystemSettingDto updateSettings(SystemSettingDto dto) {
        SystemSetting settings = systemSettingRepository.findAll().stream()
                .findFirst()
                .orElseGet(() -> new SystemSetting());

        updateFields(settings, dto);
        SystemSetting saved = systemSettingRepository.save(settings);
        return mapToDto(saved);
    }

    private SystemSettingDto createDefaultSettings() {
        SystemSetting defaultSettings = new SystemSetting();
        // defaults are already set in entity
        SystemSetting saved = systemSettingRepository.save(defaultSettings);
        return mapToDto(saved);
    }

    private SystemSettingDto mapToDto(SystemSetting s) {
        SystemSettingDto dto = new SystemSettingDto();
        dto.setId(s.getId());
        dto.setOrganizationName(s.getOrganizationName());
        dto.setLanguage(s.getLanguage());
        dto.setTimezone(s.getTimezone());
        dto.setCurrency(s.getCurrency());
        dto.setDateFormat(s.getDateFormat());
        dto.setTheme(s.getTheme());
        dto.setNotificationEmail(s.getNotificationEmail());
        dto.setEmailReports(s.isEmailReports());
        dto.setMobileNotifications(s.isMobileNotifications());
        dto.setAutoBackup(s.isAutoBackup());
        dto.setDataRetention(s.getDataRetention());
        dto.setUpdatedAt(s.getUpdatedAt());
        return dto;
    }

    private void updateFields(SystemSetting target, SystemSettingDto source) {
        target.setOrganizationName(source.getOrganizationName());
        target.setLanguage(source.getLanguage());
        target.setTimezone(source.getTimezone());
        target.setCurrency(source.getCurrency());
        target.setDateFormat(source.getDateFormat());
        target.setTheme(source.getTheme());
        target.setNotificationEmail(source.getNotificationEmail());
        target.setEmailReports(source.isEmailReports());
        target.setMobileNotifications(source.isMobileNotifications());
        target.setAutoBackup(source.isAutoBackup());
        target.setDataRetention(source.getDataRetention());
    }
}