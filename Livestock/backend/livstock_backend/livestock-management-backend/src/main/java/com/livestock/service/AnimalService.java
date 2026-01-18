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
        animal.setOwnerName(owner.getName());  // Assumes Animal has setOwnerName(String)

        Animal saved = animalRepository.save(animal);
        return modelMapper.map(saved, AnimalResponse.class);
    }

    public List<AnimalResponse> getAllAnimals(String status, String type, UUID ownerId, String search) {
        // Implement filtering logic here (example simple version)
        List<Animal> animals = animalRepository.findAllByDeletedAtIsNull();

        // Apply filters (you can enhance this with JPA Specifications later)
        if (status != null) animals = animals.stream().filter(a -> a.getStatus().equals(status)).collect(Collectors.toList());
        if (type != null) animals = animals.stream().filter(a -> a.getType().equals(type)).collect(Collectors.toList());
        if (ownerId != null) animals = animals.stream().filter(a -> a.getOwner().getId().equals(ownerId)).collect(Collectors.toList());
        if (search != null) {
            String lowerSearch = search.toLowerCase();
            animals = animals.stream()
                    .filter(a -> a.getTagId().toLowerCase().contains(lowerSearch) ||
                            a.getBreed().toLowerCase().contains(lowerSearch))
                    .collect(Collectors.toList());
        }

        return animals.stream()
                .map(a -> modelMapper.map(a, AnimalResponse.class))
                .collect(Collectors.toList());
    }

    public AnimalResponse getAnimalById(UUID id) {
        Animal animal = animalRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new RuntimeException("Animal not found with id: " + id));
        return modelMapper.map(animal, AnimalResponse.class);
    }

    public AnimalResponse updateAnimal(UUID id, AnimalRequest request) {
        Animal existing = animalRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new RuntimeException("Animal not found with id: " + id));

        modelMapper.map(request, existing);
        existing.setUpdatedAt(LocalDateTime.now());

        Animal updated = animalRepository.save(existing);
        return modelMapper.map(updated, AnimalResponse.class);
    }

    public void deleteAnimal(UUID id) {
        Animal animal = animalRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new RuntimeException("Animal not found with id: " + id));
        animal.setDeletedAt(LocalDateTime.now());
        animalRepository.save(animal);
    }
}