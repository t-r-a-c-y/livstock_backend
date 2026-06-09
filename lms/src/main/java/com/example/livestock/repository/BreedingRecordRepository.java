package com.example.livestock.repository;

import com.example.livestock.entity.BreedingRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BreedingRecordRepository extends JpaRepository<BreedingRecord, Long> {
    Page<BreedingRecord> findByActiveTrue(Pageable pageable);
    Page<BreedingRecord> findByCowOwnerIdAndActiveTrue(Long ownerId, Pageable pageable);
    Optional<BreedingRecord> findByIdAndActiveTrue(Long id);
}
