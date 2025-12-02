// OwnerRepository.java
package com.livestock.repository;

import com.livestock.entity.Owner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OwnerRepository extends JpaRepository<Owner, UUID> {

    @Query("SELECT o FROM Owner o WHERE o.deletedAt IS NULL")
    List<Owner> findAllActive();

    @Query("SELECT o FROM Owner o WHERE o.deletedAt IS NULL AND o.id = :id")
    Owner findActiveById(@Param("id") UUID id);

    boolean existsByEmailAndDeletedAtIsNull(String email);

    List<Owner> findByNameContainingIgnoreCaseAndDeletedAtIsNull(String name);
}