package com.livestock.backend.service;


import com.livestock.backend.dto.SettingsDTO;
import com.livestock.backend.model.SystemSetting;
import com.livestock.backend.repository.SystemSettingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class SystemSettingsService {
    private static final Logger logger = LoggerFactory.getLogger(SystemSettingsService.class);

    @Autowired
    private SystemSettingRepository systemSettingRepository;

    @Transactional(readOnly = true)
    @Cacheable("systemSettings")
    public SettingsDTO getSettings() {
        logger.info("Fetching system settings");
        Map<String, String> settingsMap = new HashMap<>();
        systemSettingRepository.findAll().forEach(setting -> settingsMap.put(setting.getKey(), setting.getValue()));
        SettingsDTO dto = new SettingsDTO();
        dto.setSettings(settingsMap);
        return dto;
    }

    @Transactional
    @CacheEvict(value = "systemSettings", allEntries = true)
    public void update(SettingsDTO dto) {
        logger.info("Updating system settings");
        dto.getSettings().forEach((key, value) -> {
            SystemSetting setting = systemSettingRepository.findByKey(key);
            if (setting == null) {
                setting = new SystemSetting();
                setting.setKey(key);
            }
            setting.setValue(value);
            setting.setUpdatedAt(LocalDateTime.now());
            systemSettingRepository.save(setting);
        });
    }
}
