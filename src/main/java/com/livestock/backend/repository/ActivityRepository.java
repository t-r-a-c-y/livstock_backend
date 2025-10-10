package com.livestock.backend.repository;


import com.livestock.backend.model.Activity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface ActivityRepository extends JpaRepository<Activity, UUID> {
    // Find all active activities
    @Query("SELECT a FROM Activity a WHERE a.deletedAt IS NULL")
    List<Activity> findAllActive();

    // Paginated version
    @Query("SELECT a FROM Activity a WHERE a.deletedAt IS NULL")
    Page<Activity> findAllActive(Pageable pageable);

    // Filter by type
    @Query("SELECT a FROM Activity a WHERE a.deletedAt IS NULL AND a.type = :type")
    List<Activity> findByType(@Param("type") String type);

    // Filter by date range
    @Query("SELECT a FROM Activity a WHERE a.deletedAt IS NULL AND a.date BETWEEN :startDate AND :endDate")
    List<Activity> findByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    // Filter by animalId (checks if animalId is in animal_ids array)
    @Query("SELECT a FROM Activity a WHERE a.deletedAt IS NULL AND :animalId = ANY(a.animalIds)")
    List<Activity> findByAnimalId(@Param("animalId") UUID animalId);

    // Combined filter
    @Query("SELECT a FROM Activity a WHERE a.deletedAt IS NULL " +
            "AND (:type IS NULL OR a.type = :type) " +
            "AND (:startDate IS NULL OR a.date >= :startDate) " +
            "AND (:endDate IS NULL OR a.date <= :endDate) " +
            "AND (:animalId IS NULL OR :animalId = ANY(a.animalIds))")
    Page<Activity> findByFilters(@Param("type") String type,
                                 @Param("startDate") LocalDate startDate,
                                 @Param("endDate") LocalDate endDate,
                                 @Param("animalId") UUID animalId,
                                 Pageable pageable);
}