// src/main/java/com/livestock/service/AnimalService.java
package com.livestock.service;

import com.livestock.dto.request.AnimalRequest;
import com.livestock.dto.response.AnimalResponse;
import com.livestock.entity.Animal;
import com.livestock.entity.Owner;
import com.livestock.exception.ResourceNotFoundException;
import com.livestock.repository.AnimalRepository;
import com.livestock.repository.OwnerRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnimalService {

    private final AnimalRepository animalRepository;
    private final OwnerRepository ownerRepository;
    private final ModelMapper modelMapper;

    public List<AnimalResponse> getAllAnimals(String status, String type, UUID ownerId, String search) {
        List<Animal> animals;

        if (search != null && !search.isBlank()) {
            animals = animalRepository.searchByTagIdOrBreed(search);
        } else if (ownerId != null && status != null) {
            animals = animalRepository.findByOwnerIdAndStatusAndDeletedAtIsNull(ownerId, status);
        } else if (ownerId != null) {
            animals = animalRepository.findByOwnerIdAndDeletedAtIsNull(ownerId);
        } else if (status != null) {
            animals = animalRepository.findByStatusAndDeletedAtIsNull(status);
        } else if (type != null) {
            animals = animalRepository.findByTypeAndDeletedAtIsNull(type);
        } else {
            animals = animalRepository.findAllActive();
        }

        return animals.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public AnimalResponse getAnimalById(UUID id) {
        Animal animal = animalRepository.findActiveById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Animal not found"));
        return mapToResponse(animal);
    }

    public AnimalResponse createAnimal(AnimalRequest request) {
        Owner owner = ownerRepository.findActiveById(request.getOwnerId())
                .orElseThrow(() -> new ResourceNotFoundException("Owner not found"));

        Animal animal = modelMapper.map(request, Animal.class);
        animal.setOwner(owner);
        animal = animalRepository.save(animal);

        return mapToResponse(animal);
    }

    public AnimalResponse updateAnimal(UUID id, AnimalRequest request) {
        Animal animal = animalRepository.findActiveById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Animal not found"));

        Owner owner = ownerRepository.findActiveById(request.getOwnerId())
                .orElseThrow(() -> new ResourceNotFoundException("Owner not found"));

        modelMapper.map(request, animal);
        animal.setOwner(owner);
        animal.setUpdatedAt(LocalDateTime.now());
        animal = animalRepository.save(animal);

        return mapToResponse(animal);
    }

    public void deleteAnimal(UUID id) {
        Animal animal = animalRepository.findActiveById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Animal not found"));
        animal.setDeletedAt(LocalDateTime.now());
        animalRepository.save(animal);
    }

    private AnimalResponse mapToResponse(Animal animal) {
        AnimalResponse response = modelMapper.map(animal, AnimalResponse.class);
        response.setOwnerId(animal.getOwner().getId());
        if (animal.getOwner() != null) {
            response.setOwnerName(animal.getOwner().getName());
        }
        if (animal.getParent() != null) {
            response.setParentTagId(animal.getParent().getTagId());
        }
        return response;
    }
}