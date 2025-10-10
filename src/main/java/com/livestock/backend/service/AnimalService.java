package com.livestock.backend.service;

import com.livestock.backend.dto.AnimalDTO;
import com.livestock.backend.model.Animal;
import com.livestock.backend.repository.AnimalRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AnimalService {
    private static final Logger logger = LoggerFactory.getLogger(AnimalService.class);

    @Autowired
    private AnimalRepository animalRepository;

    @Transactional(readOnly = true)
    public Page<AnimalDTO> getAll(String type, String status, UUID ownerId, Pageable pageable) {
        logger.info("Fetching animals with type: {}, status: {}, ownerId: {}", type, status, ownerId);
        Specification<Animal> spec = (root, query, cb) -> cb.isNull(root.get("deletedAt"));
        if (type != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("type"), type));
        }
        if (status != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("status"), status));
        }
        if (ownerId != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("ownerId"), ownerId));
        }
        return animalRepository.findAll(spec, pageable).map(this::toDTO);
    }

    @Transactional(readOnly = true)
    public AnimalDTO getById(UUID id) {
        logger.info("Fetching animal with id: {}", id);
        Animal animal = animalRepository.findById(id)
                .filter(a -> a.getDeletedAt() == null)
                .orElseThrow(() -> new RuntimeException("Animal not found"));
        return toDTO(animal);
    }

    @Transactional
    public AnimalDTO create(AnimalDTO dto) {
        logger.info("Creating animal with tagId: {}", dto.getTagId());
        Animal animal = new Animal();
        updateEntityFromDTO(animal, dto);
        animal.setCreatedAt(LocalDateTime.now());
        animal.setUpdatedAt(LocalDateTime.now());
        animal = animalRepository.save(animal);
        return toDTO(animal);
    }

    @Transactional
    public AnimalDTO update(UUID id, AnimalDTO dto) {
        logger.info("Updating animal with id: {}", id);
        Animal animal = animalRepository.findById(id)
                .filter(a -> a.getDeletedAt() == null)
                .orElseThrow(() -> new RuntimeException("Animal not found"));
        updateEntityFromDTO(animal, dto);
        animal.setUpdatedAt(LocalDateTime.now());
        animal = animalRepository.save(animal);
        return toDTO(animal);
    }

    @Transactional
    public void delete(UUID id) {
        logger.info("Soft deleting animal with id: {}", id);
        Animal animal = animalRepository.findById(id)
                .filter(a -> a.getDeletedAt() == null)
                .orElseThrow(() -> new RuntimeException("Animal not found"));
        animal.setDeletedAt(LocalDateTime.now());
        animalRepository.save(animal);
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
        dto.setDeletedAt(animal.getDeletedAt());
        return dto;
    }

    private void updateEntityFromDTO(Animal animal, AnimalDTO dto) {
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
}