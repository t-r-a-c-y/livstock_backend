// SystemSettingRepository.java
package com.livestock.repository;

import com.livestock.entity.SystemSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SystemSettingRepository extends JpaRepository<SystemSetting, java.util.UUID> {

    Optional<SystemSetting> findByKey(String key);

    boolean existsByKey(String key);
}