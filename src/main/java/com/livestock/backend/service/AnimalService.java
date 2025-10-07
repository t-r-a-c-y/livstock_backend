package com.livestock.backend.service;


import com.livestock.backend.dto.request.AnimalRequest;
import com.livestock.backend.dto.response.AnimalResponse;
import com.livestock.backend.exception.ResourceNotFoundException;
import com.livestock.backend.model.Animal;
import com.livestock.backend.model.Owner;
import com.livestock.backend.repository.AnimalRepository;
import com.livestock.backend.repository.OwnerRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class AnimalService {

    private final AnimalRepository animalRepository;
    private final OwnerRepository ownerRepository;
    private final AnimalMapper animalMapper;

    public AnimalService(AnimalRepository animalRepository, OwnerRepository ownerRepository, AnimalMapper animalMapper) {
        this.animalRepository = animalRepository;
        this.ownerRepository = ownerRepository;
        this.animalMapper = animalMapper;
    }

    public Page<AnimalResponse> getAllAnimals(int page, int size, String type, String status, UUID ownerId, String search) {
        Pageable pageable = PageRequest.of(page, size);
        Specification<Animal> spec = Specification.where(null);
        if (type != null) spec = spec.and((root, query, cb) -> cb.equal(root.get("type"), type));
        if (status != null) spec = spec.and((root, query, cb) -> cb.equal(root.get("status"), status));
        if (ownerId != null) spec = spec.and((root, query, cb) -> cb.equal(root.get("owner").get("id"), ownerId));
        if (search != null) spec = spec.and((root, query, cb) -> cb.or(
                cb.like(root.get("tagId"), "%" + search + "%"),
                cb.like(root.get("breed"), "%" + search + "%")
        ));
        return animalRepository.findAll(spec, pageable).map(animalMapper::toResponse);
    }

    public AnimalResponse getAnimalById(UUID id) {
        Animal animal = animalRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Animal not found"));
        return animalMapper.toResponse(animal);
    }

    public AnimalResponse createAnimal(AnimalRequest request) {
        Animal animal = animalMapper.toEntity(request);
        if (request.getOwnerId() != null) {
            Owner owner = ownerRepository.findById(request.getOwnerId()).orElseThrow(() -> new ResourceNotFoundException("Owner not found"));
            animal.setOwner(owner);
        }
        if (request.getParentId() != null) {
            Animal parent = animalRepository.findById(request.getParentId()).orElseThrow(() -> new ResourceNotFoundException("Parent not found"));
            animal.setParent(parent);
        }
        animal.setCreatedAt(new Date());
        animal.setUpdatedAt(new Date());
        animal = animalRepository.save(animal);
        return animalMapper.toResponse(animal);
    }

    public AnimalResponse updateAnimal(UUID id, AnimalRequest request) {
        Animal animal = animalRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Animal not found"));
        animal.setTagId(request.getTagId());
        animal.setType(request.getType());
        animal.setBreed(request.getBreed());
        animal.setGender(request.getGender());
        animal.setDateOfBirth(request.getDateOfBirth());
        animal.setStatus(request.getStatus());
        animal.setMilkProduction(request.getMilkProduction());
        animal.setPhoto(request.getPhoto());
        animal.setNotes(request.getNotes());
        if (request.getOwnerId() != null) {
            Owner owner = ownerRepository.findById(request.getOwnerId()).orElseThrow(() -> new ResourceNotFoundException("Owner not found"));
            animal.setOwner(owner);
        }
        if (request.getParentId() != null) {
            Animal parent = animalRepository.findById(request.getParentId()).orElseThrow(() -> new ResourceNotFoundException("Parent not found"));
            animal.setParent(parent);
        }
        animal.setUpdatedAt(new Date());
        animal = animalRepository.save(animal);
        return animalMapper.toResponse(animal);
    }

    public void deleteAnimal(UUID id) {
        if (!animalRepository.existsById(id)) {
            throw new ResourceNotFoundException("Animal not found");
        }
        animalRepository.deleteById(id);
    }

    public Map<String, Object> getAnimalStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalAnimals", animalRepository.count());
        // Add more stats using custom queries, e.g., count by type
        // For example: stats.put("byType", animalRepository.findTypeCounts()); // Add custom method if needed
        return stats;
    }
}
