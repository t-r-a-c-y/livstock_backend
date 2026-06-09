package com.example.livestock.repository;

import com.example.livestock.entity.Owner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OwnerRepository extends JpaRepository<Owner, Long> {
    Page<Owner> findByActiveTrue(Pageable pageable);
    Optional<Owner> findByIdAndActiveTrue(Long id);
    Optional<Owner> findByUserEmailAndActiveTrue(String email);
    Optional<Owner> findByUserIdAndActiveTrue(Long userId);
    boolean existsByNationalId(String nationalId);
}
