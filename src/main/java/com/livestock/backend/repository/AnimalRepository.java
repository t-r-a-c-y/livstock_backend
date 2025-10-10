package com.livestock.backend.repository;


import com.livestock.backend.model.Animal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface AnimalRepository extends JpaRepository<Animal, UUID> {
    // Find all active animals (not soft-deleted)
    @Query("SELECT a FROM Animal a WHERE a.deletedAt IS NULL")
    List<Animal> findAllActive();

    // Paginated version for list endpoint
    @Query("SELECT a FROM Animal a WHERE a.deletedAt IS NULL")
    Page<Animal> findAllActive(Pageable pageable);

    // Filter by type
    @Query("SELECT a FROM Animal a WHERE a.deletedAt IS NULL AND a.type = :type")
    List<Animal> findByType(@Param("type") String type);

    // Filter by status
    @Query("SELECT a FROM Animal a WHERE a.deletedAt IS NULL AND a.status = :status")
    List<Animal> findByStatus(@Param("status") String status);

    // Filter by ownerId
    @Query("SELECT a FROM Animal a WHERE a.deletedAt IS NULL AND a.ownerId = :ownerId")
    List<Animal> findByOwnerId(@Param("ownerId") UUID ownerId);

    // Combined filter (type, status, ownerId)
    @Query("SELECT a FROM Animal a WHERE a.deletedAt IS NULL " +
            "AND (:type IS NULL OR a.type = :type) " +
            "AND (:status IS NULL OR a.status = :status) " +
            "AND (:ownerId IS NULL OR a.ownerId = :ownerId)")
    Page<Animal> findByFilters(@Param("type") String type,
                               @Param("status") String status,
                               @Param("ownerId") UUID ownerId,
                               Pageable pageable);

    // Stats queries
    @Query("SELECT COUNT(a) FROM Animal a WHERE a.deletedAt IS NULL")
    long countTotal();

    @Query("SELECT a.type, COUNT(a) FROM Animal a WHERE a.deletedAt IS NULL GROUP BY a.type")
    List<Object[]> countByType();

    @Query("SELECT a.status, COUNT(a) FROM Animal a WHERE a.deletedAt IS NULL GROUP BY a.status")
    List<Object[]> countByStatus();
}