package com.livestock.backend.service;

import com.livestock.backend.dto.SettingsDTO;
import com.livestock.backend.model.SystemSetting;
import com.livestock.backend.repository.SystemSettingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SystemSettingsService {
    private static final Logger logger = LoggerFactory.getLogger(SystemSettingsService.class);

    @Autowired
    private SystemSettingRepository systemSettingRepository;

    @Transactional(readOnly = true)
    public SettingsDTO getByKey(String key) {
        logger.info("Fetching setting by key: {}", key);
        SystemSetting setting = systemSettingRepository.findById(key)
                .orElseThrow(() -> new RuntimeException("Setting not found"));
        return toDTO(setting);
    }

    @Transactional
    public SettingsDTO update(String key, SettingsDTO dto) {
        logger.info("Updating setting: {}", key);
        SystemSetting setting = systemSettingRepository.findById(key)
                .orElse(new SystemSetting());
        setting.setKey(key);
        setting.setValue(dto.getValue());
        setting = systemSettingRepository.save(setting);
        return toDTO(setting);
    }

    private SettingsDTO toDTO(SystemSetting setting) {
        SettingsDTO dto = new SettingsDTO();
        dto.setKey(setting.getKey());
        dto.setValue(setting.getValue());
        return dto;
    }
}