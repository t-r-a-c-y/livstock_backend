// src/main/java/com/livestock/service/AnimalService.java
package com.livestock.service;

import com.livestock.dto.request.AnimalRequest;
import com.livestock.dto.response.AnimalResponse;
import com.livestock.entity.Animal;
import com.livestock.entity.Owner;
import com.livestock.exception.ResourceNotFoundException;
import com.livestock.repository.AnimalRepository;
import com.livestock.repository.OwnerRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.beans.factory.annotation.Value;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;  // optional, for replace existing
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class AnimalService {

    private final AnimalRepository animalRepository;
    private final OwnerRepository ownerRepository;
    private final ModelMapper modelMapper;

    public AnimalService(AnimalRepository animalRepository, OwnerRepository ownerRepository, ModelMapper modelMapper) {
        this.animalRepository = animalRepository;
        this.ownerRepository = ownerRepository;
        this.modelMapper = modelMapper;
    }

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
                .toList();
    }

    public AnimalResponse getAnimalById(UUID id) {
        Animal animal = animalRepository.findActiveById(id);
        if (animal == null) {
            throw new ResourceNotFoundException("Animal not found");
        }
        return mapToResponse(animal);
    }

    public AnimalResponse createAnimal(AnimalRequest request) {
        Owner owner = ownerRepository.findByIdAndDeletedAtIsNull(ownerId)
                .orElseThrow(() -> new RuntimeException("Owner not found"));

        Animal animal = modelMapper.map(request, Animal.class);
        animal.setOwner(owner);
        animal = animalRepository.save(animal);

        return mapToResponse(animal);
    }


    public AnimalResponse updateAnimal(UUID id, AnimalRequest request) {
        Animal animal = animalRepository.findActiveById(id);
        if (animal == null) {
            throw new ResourceNotFoundException("Animal not found");
        }

        Owner owner = ownerRepository.findActiveById(request.getOwnerId());
        if (owner == null) {
            throw new ResourceNotFoundException("Owner not found");
        }

        modelMapper.map(request, animal);
        animal.setOwner(owner);
        animal.setUpdatedAt(LocalDateTime.now());
        animal = animalRepository.save(animal);

        return mapToResponse(animal);
    }

    public void deleteAnimal(UUID id) {
        Animal animal = animalRepository.findActiveById(id);
        if (animal == null) {
            throw new ResourceNotFoundException("Animal not found");
        }
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

    @Value("${app.upload.dir}")
    private String uploadDir;

    public AnimalResponse uploadAnimalPhoto(UUID animalId, MultipartFile file) {
        Animal animal = animalRepository.findActiveById(animalId);
        if (animal == null) {
            throw new ResourceNotFoundException("Animal not found");
        }

        if (file.isEmpty()) {
            throw new IllegalArgumentException("Uploaded file is empty");
        }

        try {
            // Ensure upload directory exists
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Generate unique filename to avoid conflicts
            String originalFilename = file.getOriginalFilename();
            String filename = UUID.randomUUID().toString() + "_" + originalFilename;
            Path filePath = uploadPath.resolve(filename);

            // Save the file
            Files.copy(file.getInputStream(), filePath);

            // Save the relative URL in database
            String photoUrl = "/uploads/" + filename;
            animal.setPhoto(photoUrl);
            animalRepository.save(animal);

            return mapToResponse(animal);

        } catch (Exception e) {
            throw new RuntimeException("Failed to upload photo: " + e.getMessage(), e);
        }
    }
}