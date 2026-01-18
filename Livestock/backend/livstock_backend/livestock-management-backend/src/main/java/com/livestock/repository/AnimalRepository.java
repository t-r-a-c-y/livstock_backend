// AnimalRepository.java
package com.livestock.repository;

import com.livestock.entity.Animal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AnimalRepository extends JpaRepository<Animal, UUID> {

    // Active animals only (not soft-deleted)
    @Query("SELECT a FROM Animal a WHERE a.deletedAt IS NULL")
    List<Animal> findAllActive();

    @Query("SELECT a FROM Animal a WHERE a.deletedAt IS NULL AND a.id = :id")
    Animal findActiveById(@Param("id") UUID id);
    List<Animal> findAllByDeletedAtIsNull();

    Optional<Animal> findByIdAndDeletedAtIsNull(UUID id);

    // Filters used by frontend
    List<Animal> findByOwnerIdAndDeletedAtIsNull(UUID ownerId);

    List<Animal> findByStatusAndDeletedAtIsNull(String status);

    List<Animal> findByTypeAndDeletedAtIsNull(String type);

    List<Animal> findByOwnerIdAndStatusAndDeletedAtIsNull(UUID ownerId, String status);

    List<Animal> findByOwnerIdAndTypeAndDeletedAtIsNull(UUID ownerId, String type);

    // Search by tagId or breed (case-insensitive partial match)
    @Query("SELECT a FROM Animal a WHERE a.deletedAt IS NULL " +
            "AND (LOWER(a.tagId) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "OR LOWER(a.breed) LIKE LOWER(CONCAT('%', :search, '%')))")
    List<Animal> searchByTagIdOrBreed(@Param("search") String search);

    @Query("SELECT a.tagId, a.type, a.breed, a.gender, a.dateOfBirth, o.name, a.status, a.milk " +
            "FROM Animal a LEFT JOIN a.owner o WHERE a.deletedAt IS NULL")
    List<Object[]> findAllForReport();
}