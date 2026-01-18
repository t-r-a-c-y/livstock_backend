package com.livestock.service;

import com.livestock.dto.request.AnimalRequest;
import com.livestock.dto.response.AnimalResponse;
import com.livestock.entity.Animal;
import com.livestock.entity.Owner;
import com.livestock.repository.AnimalRepository;
import com.livestock.repository.OwnerRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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

    public AnimalResponse createAnimal(AnimalRequest request, UUID ownerId) {
        Owner owner = ownerRepository.findByIdAndDeletedAtIsNull(ownerId)
                .orElseThrow(() -> new RuntimeException("Owner not found with id: " + ownerId));

        Animal animal = modelMapper.map(request, Animal.class);
        animal.setOwner(owner);
        animal.setOwnerName(owner.getName());

        Animal saved = animalRepository.save(animal);
        return modelMapper.map(saved, AnimalResponse.class);
    }

    public List<AnimalResponse> getAllAnimals(String status, String type, UUID ownerId, String search) {
        List<Animal> animals = animalRepository.findAllByDeletedAtIsNull();

        if (status != null) {
            animals = animals.stream().filter(a -> status.equals(a.getStatus())).collect(Collectors.toList());
        }
        if (type != null) {
            animals = animals.stream().filter(a -> type.equals(a.getType())).collect(Collectors.toList());
        }
        if (ownerId != null) {
            animals = animals.stream().filter(a -> ownerId.equals(a.getOwner().getId())).collect(Collectors.toList());
        }
        if (search != null) {
            String lower = search.toLowerCase();
            animals = animals.stream()
                    .filter(a -> a.getTagId().toLowerCase().contains(lower) ||
                            a.getBreed().toLowerCase().contains(lower))
                    .collect(Collectors.toList());
        }

        return animals.stream()
                .map(a -> modelMapper.map(a, AnimalResponse.class))
                .collect(Collectors.toList());
    }

    public AnimalResponse getAnimalById(UUID id) {
        Animal animal = animalRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new RuntimeException("Animal not found"));
        return modelMapper.map(animal, AnimalResponse.class);
    }

    public AnimalResponse updateAnimal(UUID id, AnimalRequest request) {
        Animal existing = animalRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new RuntimeException("Animal not found"));
        modelMapper.map(request, existing);
        existing.setUpdatedAt(LocalDateTime.now());
        Animal updated = animalRepository.save(existing);
        return modelMapper.map(updated, AnimalResponse.class);
    }

    public void deleteAnimal(UUID id) {
        Animal animal = animalRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new RuntimeException("Animal not found"));
        animal.setDeletedAt(LocalDateTime.now());
        animalRepository.save(animal);
    }

    // Add this method for photo upload
    public AnimalResponse uploadAnimalPhoto(UUID id, MultipartFile photo) {
        Animal animal = animalRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new RuntimeException("Animal not found"));
        // Implement photo upload logic here (e.g. save file, update animal.photo)
        // For example:
        // String photoPath = uploadFile(photo);
        // animal.setPhoto(photoPath);
        animal.setUpdatedAt(LocalDateTime.now());
        Animal updated = animalRepository.save(animal);
        return modelMapper.map(updated, AnimalResponse.class);
    }
}