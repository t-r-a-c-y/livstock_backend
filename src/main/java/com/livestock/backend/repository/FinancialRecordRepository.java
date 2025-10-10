package com.livestock.backend.repository;

import com.livestock.backend.model.FinancialRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface FinancialRecordRepository extends JpaRepository<FinancialRecord, UUID> {
    // Find all active records
    @Query("SELECT f FROM FinancialRecord f WHERE f.deletedAt IS NULL")
    List<FinancialRecord> findAllActive();

    // Paginated version
    @Query("SELECT f FROM FinancialRecord f WHERE f.deletedAt IS NULL")
    Page<FinancialRecord> findAllActive(Pageable pageable);

    // Filter by type (income/expense)
    @Query("SELECT f FROM FinancialRecord f WHERE f.deletedAt IS NULL AND f.type = :type")
    List<FinancialRecord> findByType(@Param("type") String type);

    // Filter by date range
    @Query("SELECT f FROM FinancialRecord f WHERE f.deletedAt IS NULL AND f.date BETWEEN :startDate AND :endDate")
    List<FinancialRecord> findByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    // Combined filter
    @Query("SELECT f FROM FinancialRecord f WHERE f.deletedAt IS NULL " +
            "AND (:type IS NULL OR f.type = :type) " +
            "AND (:startDate IS NULL OR f.date >= :startDate) " +
            "AND (:endDate IS NULL OR f.date <= :endDate)")
    Page<FinancialRecord> findByFilters(@Param("type") String type,
                                        @Param("startDate") LocalDate startDate,
                                        @Param("endDate") LocalDate endDate,
                                        Pageable pageable);

    // Summary queries
    @Query("SELECT SUM(f.amount) FROM FinancialRecord f WHERE f.deletedAt IS NULL AND f.type = 'income'")
    Double getIncomeSummary();

    @Query("SELECT SUM(f.amount) FROM FinancialRecord f WHERE f.deletedAt IS NULL AND f.type = 'expense'")
    Double getExpenseSummary();
}