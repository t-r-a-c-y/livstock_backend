package com.livestock.backend.service;


import com.livestock.backend.dto.AnimalDTO;
import com.livestock.backend.dto.AnimalStatsDTO;
import com.livestock.backend.dto.AnimalWithOwnerDTO;
import com.livestock.backend.model.Animal;
import com.livestock.backend.model.Owner;
import com.livestock.backend.repository.AnimalRepository;
import com.livestock.backend.repository.OwnerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AnimalService {
    private static final Logger logger = LoggerFactory.getLogger(AnimalService.class);

    @Autowired
    private AnimalRepository animalRepository;

    @Autowired
    private OwnerRepository ownerRepository;

    @Transactional(readOnly = true)
    public Page<AnimalDTO> getAll(String type, String status, UUID ownerId, Pageable pageable) {
        logger.info("Fetching animals with filters: type={}, status={}, ownerId={}", type, status, ownerId);
        Specification<Animal> spec = Specification.where((root, query, cb) -> cb.isNull(root.get("deletedAt")));
        if (type != null) spec = spec.and((root, query, cb) -> cb.equal(root.get("type"), type));
        if (status != null) spec = spec.and((root, query, cb) -> cb.equal(root.get("status"), status));
        if (ownerId != null) spec = spec.and((root, query, cb) -> cb.equal(root.get("ownerId"), ownerId));
        return animalRepository.findAll(spec, pageable).map(this::toDTO);
    }

    @Transactional(readOnly = true)
    public AnimalWithOwnerDTO getById(UUID id) {
        logger.info("Fetching animal by ID: {}", id);
        Animal animal = animalRepository.findById(id).orElseThrow(() -> new RuntimeException("Animal not found"));
        if (animal.getDeletedAt() != null) throw new RuntimeException("Animal deleted");
        Owner owner = ownerRepository.findById(animal.getOwnerId()).orElseThrow(() -> new RuntimeException("Owner not found"));
        AnimalWithOwnerDTO dto = new AnimalWithOwnerDTO();
        dto.setAnimal(toDTO(animal));
        dto.setOwner(ownerToDTO(owner));
        return dto;
    }

    @Transactional
    public AnimalDTO create(AnimalDTO dto) {
        logger.info("Creating animal: {}", dto.getTagId());
        Animal animal = new Animal();
        mapToEntity(dto, animal);
        animal.setCreatedAt(LocalDateTime.now());
        animal.setUpdatedAt(LocalDateTime.now());
        animal = animalRepository.save(animal);
        return toDTO(animal);
    }

    @Transactional
    public AnimalDTO update(UUID id, AnimalDTO dto) {
        logger.info("Updating animal: {}", id);
        Animal animal = animalRepository.findById(id).orElseThrow(() -> new RuntimeException("Animal not found"));
        if (animal.getDeletedAt() != null) throw new RuntimeException("Animal deleted");
        mapToEntity(dto, animal);
        animal.setUpdatedAt(LocalDateTime.now());
        animal = animalRepository.save(animal);
        return toDTO(animal);
    }

    @Transactional
    public void softDelete(UUID id) {
        logger.info("Soft deleting animal: {}", id);
        Animal animal = animalRepository.findById(id).orElseThrow(() -> new RuntimeException("Animal not found"));
        animal.setDeletedAt(LocalDateTime.now());
        animalRepository.save(animal);
    }

    @Transactional(readOnly = true)
    @Cacheable("animalStats")
    public AnimalStatsDTO getStats() {
        logger.info("Fetching animal stats");
        return animalRepository.getStats();
    }

    private AnimalDTO toDTO(Animal animal) {
        AnimalDTO dto = new AnimalDTO();
        dto.setId(animal.getId());
        dto.setTagId(animal.getTagId());
        dto.setType(animal.getType());
        dto.setBreed(animal.getBreed());
        dto.setGender(animal.getGender());
        dto.setDateOfBirth(animal.getDateOfBirth());
        dto.setOwnerId(animal.getOwnerId());
        dto.setStatus(animal.getStatus());
        dto.setMilkProduction(animal.getMilkProduction());
        dto.setPhoto(animal.getPhoto());
        dto.setParentId(animal.getParentId());
        dto.setSalePrice(animal.getSalePrice());
        dto.setNotes(animal.getNotes());
        dto.setCreatedAt(animal.getCreatedAt());
        dto.setUpdatedAt(animal.getUpdatedAt());
        return dto;  // Exclude deletedAt
    }

    private void mapToEntity(AnimalDTO dto, Animal animal) {
        animal.setTagId(dto.getTagId());
        animal.setType(dto.getType());
        animal.setBreed(dto.getBreed());
        animal.setGender(dto.getGender());
        animal.setDateOfBirth(dto.getDateOfBirth());
        animal.setOwnerId(dto.getOwnerId());
        animal.setStatus(dto.getStatus());
        animal.setMilkProduction(dto.getMilkProduction());
        animal.setPhoto(dto.getPhoto());
        animal.setParentId(dto.getParentId());
        animal.setSalePrice(dto.getSalePrice());
        animal.setNotes(dto.getNotes());
    }

    private OwnerDTO ownerToDTO(Owner owner) {
        OwnerDTO dto = new OwnerDTO();
        dto.setId(owner.getId());
        dto.setName(owner.getName());
        dto.setPhone(owner.getPhone());
        dto.setEmail(owner.getEmail());
        dto.setAddress(owner.getAddress());
        dto.setNationalId(owner.getNationalId());
        dto.setBankAccount(owner.getBankAccount());
        dto.setEmergencyContact(owner.getEmergencyContact());
        dto.setCreatedAt(owner.getCreatedAt());
        dto.setUpdatedAt(owner.getUpdatedAt());
        return dto;
    }

    private UUID getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth.getPrincipal() instanceof UserPrincipal principal) {
            return principal.getId();
        }
        throw new RuntimeException("No authenticated user");
    }
}