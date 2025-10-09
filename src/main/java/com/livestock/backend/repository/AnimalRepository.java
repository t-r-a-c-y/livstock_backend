package com.livestock.backend.repository;

import com.livestock.backend.dto.AnimalStatsDTO;
import com.livestock.backend.model.Animal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface AnimalRepository extends JpaRepository<Animal, UUID>, JpaSpecificationExecutor<Animal> {
    long countByOwnerIdAndDeletedAtIsNull(UUID ownerId);

    @Query("SELECT new com.example.livestockbackend.dto.AnimalStatsDTO(" +
            "COUNT(a), " +
            "COUNT(CASE WHEN a.type = 'cow' THEN 1 END), " +
            "COUNT(CASE WHEN a.type = 'calf' THEN 1 END), " +
            "COUNT(CASE WHEN a.type = 'goat' THEN 1 END), " +
            "COUNT(CASE WHEN a.type = 'kid' THEN 1 END), " +
            "COUNT(CASE WHEN a.status = 'healthy' THEN 1 END), " +
            "COUNT(CASE WHEN a.status = 'sick' THEN 1 END), " +
            "COUNT(CASE WHEN a.status = 'sold' THEN 1 END), " +
            "COUNT(CASE WHEN a.status = 'dead' THEN 1 END) " +
            ") FROM Animal a WHERE a.deletedAt IS NULL")
    AnimalStatsDTO getStats();
}