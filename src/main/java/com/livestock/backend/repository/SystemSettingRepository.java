package com.livestock.backend.repository;


import com.livestock.backend.model.SystemSetting;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SystemSettingRepository extends JpaRepository<SystemSetting, UUID> {
    SystemSetting findByKey(String key);
}