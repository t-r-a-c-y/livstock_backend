// FinancialRecordRepository.java
package com.livestock.repository;

import com.livestock.entity.FinancialRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface FinancialRecordRepository extends JpaRepository<FinancialRecord, UUID> {

    @Query("SELECT f FROM FinancialRecord f WHERE f.deletedAt IS NULL")
    List<FinancialRecord> findAllActive();

    @Query("SELECT f FROM FinancialRecord f WHERE f.deletedAt IS NULL AND f.id = :id")
    FinancialRecord findActiveById(@Param("id") UUID id);

    List<FinancialRecord> findByTypeAndDeletedAtIsNull(String type);

    List<FinancialRecord> findByOwnerIdAndDeletedAtIsNull(UUID ownerId);

    List<FinancialRecord> findByAnimalIdAndDeletedAtIsNull(UUID animalId);

    @Query("SELECT f FROM FinancialRecord f WHERE f.deletedAt IS NULL " +
            "AND f.date >= :from AND f.date <= :to")
    List<FinancialRecord> ﬁndByDateRange(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    @Query("SELECT f FROM FinancialRecord f WHERE f.deletedAt IS NULL " +
            "AND f.category = :category")
    List<FinancialRecord> findByCategory(@Param("category") String category);

    @Query("SELECT f FROM FinancialRecord f WHERE f.deletedAt IS NULL AND f.date BETWEEN :from AND :to")
    List<FinancialRecord> findByDateRange(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);
}