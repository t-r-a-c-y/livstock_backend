package com.livestock.backend.repository;

import com.livestock.backend.model.SystemSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface SystemSettingRepository extends JpaRepository<SystemSetting, UUID> {
    // Typically, only one system settings record exists
    @Query("SELECT s FROM SystemSetting s")
    Optional<SystemSetting> findFirst();
}