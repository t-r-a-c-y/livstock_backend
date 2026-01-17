package com.livestock.repository;

import com.livestock.entity.Owner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OwnerRepository extends JpaRepository<Owner, UUID> {

    // Find all active (not deleted) owners
    List<Owner> findAllByDeletedAtIsNull();

    // Find one active owner by ID
    Optional<Owner> findByIdAndDeletedAtIsNull(UUID id);
}