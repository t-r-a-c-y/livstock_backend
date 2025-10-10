package com.livestock.backend.repository;


import com.livestock.backend.model.Owner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface OwnerRepository extends JpaRepository<Owner, UUID> {
    // Find all active owners
    @Query("SELECT o FROM Owner o WHERE o.deletedAt IS NULL")
    List<Owner> findAllActive();

    // Paginated version
    @Query("SELECT o FROM Owner o WHERE o.deletedAt IS NULL")
    Page<Owner> findAllActive(Pageable pageable);

    // Get owner with animal count
    @Query("SELECT o, (SELECT COUNT(a) FROM Animal a WHERE a.ownerId = o.id AND a.deletedAt IS NULL) AS animalCount " +
            "FROM Owner o WHERE o.id = :id AND o.deletedAt IS NULL")
    Object[] findByIdWithAnimalCount(@Param("id") UUID id);
}