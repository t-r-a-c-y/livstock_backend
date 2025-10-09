package com.livestock.backend.repository;

import com.livestock.backend.dto.FinancialSummaryDTO;
import com.livestock.backend.model.FinancialRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface FinancialRecordRepository extends JpaRepository<FinancialRecord, UUID>, JpaSpecificationExecutor<FinancialRecord> {
    @Query("SELECT new com.livestock.backend.dto.FinancialSummaryDTO(" +
            "COALESCE(SUM(CASE WHEN f.type = 'income' THEN f.amount ELSE 0 END), 0), " +
            "COALESCE(SUM(CASE WHEN f.type = 'expense' THEN f.amount ELSE 0 END), 0), " +
            "COALESCE(SUM(CASE WHEN f.type = 'income' THEN f.amount ELSE -f.amount END), 0) " +
            ") FROM FinancialRecord f WHERE f.deletedAt IS NULL")
    FinancialSummaryDTO getSummary();
}