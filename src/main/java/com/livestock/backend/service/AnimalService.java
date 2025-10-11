package com.livestock.backend.service;

import com.livestock.backend.dto.AnimalDTO;
import com.livestock.backend.dto.AnimalCreateDTO;
import com.livestock.backend.dto.AnimalUpdateDTO;
import com.livestock.backend.model.Animal;
import com.livestock.backend.repository.AnimalRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AnimalService {
    private static final Logger logger = LoggerFactory.getLogger(AnimalService.class);
    private final AnimalRepository animalRepository;
    private final ModelMapper modelMapper;

    public Page<AnimalDTO> getAllAnimals(String type, String status, UUID ownerId, Pageable pageable) {
        logger.info("Fetching animals with filters: type={}, status={}, ownerId={}", type, status, ownerId);
        return animalRepository.findByFilters(type, status, ownerId, pageable)
                .map(animal -> modelMapper.map(animal, AnimalDTO.class));
    }

    public AnimalDTO getAnimalById(UUID id) {
        logger.info("Fetching animal with id: {}", id);
        Animal animal = animalRepository.findById(id)
                .filter(a -> a.getDeletedAt() == null)
                .orElseThrow(() -> new RuntimeException("Animal not found or deleted"));
        return modelMapper.map(animal, AnimalDTO.class);
    }

    @Transactional
    public AnimalDTO createAnimal(AnimalCreateDTO dto) {
        logger.info("Creating new animal with tagId: {}", dto.getTagId());
        Animal animal = modelMapper.map(dto, Animal.class);
        animal.setCreatedAt(LocalDateTime.now());
        animal.setUpdatedAt(LocalDateTime.now());
        animal = animalRepository.save(animal);
        return modelMapper.map(animal, AnimalDTO.class);
    }

    @Transactional
    public AnimalDTO updateAnimal(UUID id, AnimalUpdateDTO dto) {
        logger.info("Updating animal with id: {}", id);
        Animal animal = animalRepository.findById(id)
                .filter(a -> a.getDeletedAt() == null)
                .orElseThrow(() -> new RuntimeException("Animal not found or deleted"));
        modelMapper.map(dto, animal);
        animal.setUpdatedAt(LocalDateTime.now());
        animal = animalRepository.save(animal);
        return modelMapper.map(animal, AnimalDTO.class);
    }

    @Transactional
    public void softDeleteAnimal(UUID id) {
        logger.info("Soft deleting animal with id: {}", id);
        Animal animal = animalRepository.findById(id)
                .filter(a -> a.getDeletedAt() == null)
                .orElseThrow(() -> new RuntimeException("Animal not found or already deleted"));
        animal.setDeletedAt(LocalDateTime.now());
        animalRepository.save(animal);
    }

    public Map<String, Object> getAnimalStats() {
        logger.info("Fetching animal statistics");
        Map<String, Object> stats = new HashMap<>();
        stats.put("total", animalRepository.countTotal());
        stats.put("byType", animalRepository.countByType());
        stats.put("byStatus", animalRepository.countByStatus());
        return stats;
    }
}