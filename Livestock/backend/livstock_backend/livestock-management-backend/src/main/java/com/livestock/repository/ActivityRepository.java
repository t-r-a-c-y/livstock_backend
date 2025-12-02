// ActivityRepository.java
package com.livestock.repository;

import com.livestock.entity.Activity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, UUID> {

    @Query("SELECT a FROM Activity a WHERE a.deletedAt IS NULL")
    List<Activity> findAllActive();

    @Query("SELECT a FROM Activity a WHERE a.deletedAt IS NULL AND a.id = :id")
    Activity findActiveById(@Param("id") UUID id);

    // Filters used by frontend
    List<Activity> findByTypeAndDeletedAtIsNull(String type);

    @Query("SELECT a FROM Activity a JOIN a.animals an WHERE an.id = :animalId AND a.deletedAt IS NULL")
    List<Activity> findByAnimalId(@Param("animalId") UUID animalId);

    @Query("SELECT a FROM Activity a WHERE a.deletedAt IS NULL " +
            "AND a.date >= :from AND a.date <= :to")
    List<Activity> findByDateRange(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);
}