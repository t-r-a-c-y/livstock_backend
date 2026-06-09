package com.example.livestock.repository;

import com.example.livestock.entity.Animal;
import com.example.livestock.enums.AnimalStatus;
import com.example.livestock.enums.AnimalType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface AnimalRepository extends JpaRepository<Animal, Long> {
    Page<Animal> findByActiveTrue(Pageable pageable);
    Page<Animal> findByActive(boolean active, Pageable pageable);
    Page<Animal> findByOwnerIdAndActiveTrue(Long ownerId, Pageable pageable);
    Page<Animal> findByAnimalTypeAndActiveTrue(AnimalType animalType, Pageable pageable);
    Page<Animal> findByAnimalStatusAndActiveTrue(AnimalStatus status, Pageable pageable);
    Page<Animal> findByCreatedAtBetweenAndActiveTrue(LocalDateTime from, LocalDateTime to, Pageable pageable);
    Optional<Animal> findByIdAndActiveTrue(Long id);
    Optional<Animal> findByIdAndOwnerIdAndActiveTrue(Long id, Long ownerId);
    boolean existsByTagNumber(String tagNumber);
}
