// ActivityRepository.java
package com.livestock.repository;

import com.livestock.entity.Activity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, UUID> {

    List<Activity> findByType(String type);  // or use ActivityType enum

    List<Activity> findByDateBetween(LocalDate start, LocalDate end);

    @Query("SELECT a FROM Activity a JOIN a.animals animal WHERE animal.id = :animalId")
    List<Activity> findByAnimalId(UUID animalId);

    @Query("SELECT a FROM Activity a WHERE a.createdBy.id = :userId")
    List<Activity> findByCreatedById(UUID userId);

    List<Activity> findByDate(LocalDate date);
}