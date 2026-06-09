package com.example.livestock.repository;

import com.example.livestock.entity.HealthRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface HealthRecordRepository extends JpaRepository<HealthRecord, Long> {
    Page<HealthRecord> findByActiveTrue(Pageable pageable);
    Page<HealthRecord> findByAnimalOwnerIdAndActiveTrue(Long ownerId, Pageable pageable);
    Optional<HealthRecord> findByIdAndActiveTrue(Long id);
}
