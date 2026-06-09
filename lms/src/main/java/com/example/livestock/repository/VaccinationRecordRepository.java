package com.example.livestock.repository;

import com.example.livestock.entity.VaccinationRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface VaccinationRecordRepository extends JpaRepository<VaccinationRecord, Long> {
    Page<VaccinationRecord> findByActiveTrue(Pageable pageable);
    Page<VaccinationRecord> findByAnimalOwnerIdAndActiveTrue(Long ownerId, Pageable pageable);
    Optional<VaccinationRecord> findByIdAndActiveTrue(Long id);
    List<VaccinationRecord> findByNextDueDateBetweenAndActiveTrue(LocalDate from, LocalDate to);
}
