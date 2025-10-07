package com.livestock.backend.repository;

import com.livestock.backend.model.FinancialRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface FinancialRecordRepository extends JpaRepository<FinancialRecord, UUID> {
    Page<FinancialRecord> findByOwnerId(UUID ownerId, Pageable pageable);
    List<FinancialRecord> findByOwnerId(UUID ownerId);
    long countByOwnerId(UUID ownerId);
}