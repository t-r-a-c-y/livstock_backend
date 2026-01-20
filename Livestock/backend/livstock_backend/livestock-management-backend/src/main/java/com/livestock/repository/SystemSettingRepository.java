// SystemSettingRepository.java
package com.livestock.repository;

import com.livestock.entity.SystemSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SystemSettingRepository extends JpaRepository<SystemSetting, UUID> {

    // Usually there's only one settings record
    // You can use findFirst() or limit 1 in queries if needed
}