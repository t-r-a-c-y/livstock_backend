// OwnerRepository.java
package com.livestock.repository;

import com.livestock.entity.Owner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface OwnerRepository extends JpaRepository<Owner, UUID> {

    Optional<Owner> findByEmail(String email);

    Optional<Owner> findByPhone(String phone);

    Optional<Owner> findByNationalId(String nationalId);

    // Useful for searching owners
    List<Owner> findByNameContainingIgnoreCase(String namePart);
}