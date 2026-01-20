// FinancialRecordRepository.java
package com.livestock.repository;

import com.livestock.entity.FinancialRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface FinancialRecordRepository extends JpaRepository<FinancialRecord, UUID> {

    List<FinancialRecord> findByType(String type);  // INCOME / EXPENSE

    List<FinancialRecord> findByOwnerId(UUID ownerId);

    List<FinancialRecord> findByAnimalId(UUID animalId);

    List<FinancialRecord> findByDateBetween(LocalDate start, LocalDate end);

    @Query("SELECT SUM(fr.amount) FROM FinancialRecord fr WHERE fr.type = 'INCOME' AND fr.date BETWEEN :start AND :end")
    BigDecimal sumIncomeBetween(LocalDate start, LocalDate end);

    @Query("SELECT SUM(fr.amount) FROM FinancialRecord fr WHERE fr.type = 'EXPENSE' AND fr.date BETWEEN :start AND :end")
    BigDecimal sumExpenseBetween(LocalDate start, LocalDate end);

    @Query("SELECT fr FROM FinancialRecord fr WHERE fr.category = :category")
    List<FinancialRecord> findByCategory(String category);
}