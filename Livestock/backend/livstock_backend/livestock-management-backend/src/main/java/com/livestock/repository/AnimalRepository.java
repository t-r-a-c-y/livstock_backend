// AnimalRepository.java
package com.livestock.repository;

import com.livestock.entity.Animal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AnimalRepository extends JpaRepository<Animal, UUID> {

    Optional<Animal> findByTagId(String tagId);

    List<Animal> findByOwnerId(UUID ownerId);

    List<Animal> findByType(String type);  // Note: you might want to use AnimalType enum directly

    List<Animal> findByStatus(String status);  // or use AnimalStatus enum

    @Query("SELECT a FROM Animal a WHERE a.parent.id = :parentId")
    List<Animal> findChildrenByParentId(UUID parentId);

    // Example useful query for dashboard/health status
    @Query("SELECT COUNT(a) FROM Animal a WHERE a.status = 'SICK'")
    long countSickAnimals();

    // You can add more specific queries as needed
}