package com.livestock.backend.service;

import com.livestock.backend.dto.request.AnimalRequest;
import com.livestock.backend.dto.response.AnimalResponse;
import com.livestock.backend.exception.ResourceNotFoundException;
import com.livestock.backend.model.Animal;
import com.livestock.backend.model.UserProfile;
import com.livestock.backend.repository.AnimalRepository;
import com.livestock.backend.repository.OwnerRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class AnimalService {

    private final AnimalRepository animalRepository;
    private final OwnerRepository ownerRepository;
    private final AnimalMapper animalMapper;
    private final AuthService authService;

    public AnimalService(AnimalRepository animalRepository, OwnerRepository ownerRepository,
                         AnimalMapper animalMapper, AuthService authService) {
        this.animalRepository = animalRepository;
        this.ownerRepository = ownerRepository;
        this.animalMapper = animalMapper;
        this.authService = authService;
    }

    public AnimalResponse createAnimal(AnimalRequest request) {
        UserProfile currentUser = authService.getCurrentUser();
        Animal animal = animalMapper.toEntity(request);
        animal.setOwner(currentUser.getOwner());
        animal = animalRepository.save(animal);
        return animalMapper.toResponse(animal);
    }

    public AnimalResponse getAnimalById(UUID id) {
        Animal animal = animalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Animal not found with id: " + id));
        return animalMapper.toResponse(animal);
    }

    public Page<AnimalResponse> getAllAnimals(Pageable pageable, String species, String breed, String gender) {
        UserProfile currentUser = authService.getCurrentUser();
        Specification<Animal> spec = (root, query, cb) -> {
            if (currentUser.getOwner() == null) {
                return cb.conjunction();
            }
            var predicates = cb.conjunction();
            predicates = cb.and(predicates, cb.equal(root.get("owner").get("id"), currentUser.getOwner().getId()));
            if (species != null && !species.isEmpty()) {
                predicates = cb.and(predicates, cb.equal(root.get("species"), species));
            }
            if (breed != null && !breed.isEmpty()) {
                predicates = cb.and(predicates, cb.equal(root.get("breed"), breed));
            }
            if (gender != null && !gender.isEmpty()) {
                predicates = cb.and(predicates, cb.equal(root.get("gender"), gender));
            }
            return predicates;
        };
        return animalRepository.findAll(spec, pageable).map(animalMapper::toResponse);
    }

    public List<AnimalResponse> getAllAnimals() {
        UserProfile currentUser = authService.getCurrentUser();
        List<Animal> animals = animalRepository.findByOwnerId(currentUser.getOwner().getId());
        return animals.stream().map(animalMapper::toResponse).toList();
    }

    public AnimalResponse updateAnimal(UUID id, AnimalRequest request) {
        Animal animal = animalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Animal not found with id: " + id));
        UserProfile currentUser = authService.getCurrentUser();
        if (!animal.getOwner().getId().equals(currentUser.getOwner().getId())) {
            throw new SecurityException("Unauthorized to update this animal");
        }
        Animal updatedAnimal = animalMapper.toEntity(request);
        updatedAnimal.setId(id);
        updatedAnimal.setOwner(animal.getOwner());
        updatedAnimal = animalRepository.save(updatedAnimal);
        return animalMapper.toResponse(updatedAnimal);
    }

    public void deleteAnimal(UUID id) {
        Animal animal = animalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Animal not found with id: " + id));
        UserProfile currentUser = authService.getCurrentUser();
        if (!animal.getOwner().getId().equals(currentUser.getOwner().getId())) {
            throw new SecurityException("Unauthorized to delete this animal");
        }
        animalRepository.delete(animal);
    }
}