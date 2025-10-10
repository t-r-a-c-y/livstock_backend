package com.livestock.backend.repository;

import com.livestock.backend.model.FinancialRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface FinancialRecordRepository extends JpaRepository<FinancialRecord, UUID>, JpaSpecificationExecutor<FinancialRecord> {
}