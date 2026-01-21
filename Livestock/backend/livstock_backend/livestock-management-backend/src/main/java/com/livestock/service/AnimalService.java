// AnimalService.java
package com.livestock.service;

import com.livestock.dto.AnimalDto;
import com.livestock.entity.Animal;
import com.livestock.entity.Owner;
import com.livestock.entity.enums.AnimalStatus;
import com.livestock.entity.enums.AnimalType;
import com.livestock.entity.enums.Gender;
import com.livestock.repository.AnimalRepository;
import com.livestock.repository.OwnerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnimalService {

    private final AnimalRepository animalRepository;
    private final OwnerRepository ownerRepository;
    private final NotificationService notificationService;

    @Transactional
    public AnimalDto createAnimal(AnimalDto dto) {
        Animal animal = mapToEntity(dto);
        validateAnimal(animal);
        Animal saved = animalRepository.save(animal);
        if (saved.getStatus() == AnimalStatus.SICK) {
            notificationService.createHealthAlert("Animal sick", saved.getTagId(), saved.getId());
        }
        return mapToDto(saved);
    }

    @Transactional
    public AnimalDto updateAnimal(UUID id, AnimalDto dto) {
        Animal existing = animalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Animal not found"));
        updateFields(existing, dto);
        validateAnimal(existing);
        Animal saved = animalRepository.save(existing);
        if (saved.getStatus() == AnimalStatus.SICK) {
            notificationService.createHealthAlert("Animal status changed to sick", saved.getTagId(), saved.getId());
        }
        return mapToDto(saved);
    }

    @Transactional(readOnly = true)
    public AnimalDto getAnimalById(UUID id) {
        return mapToDto(animalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Animal not found")));
    }

    @Transactional(readOnly = true)
    public List<AnimalDto> getAllAnimals() {
        return animalRepository.findAll().stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Transactional
    public void deleteAnimal(UUID id) {
        if (!animalRepository.existsById(id)) {
            throw new RuntimeException("Animal not found");
        }
        animalRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<AnimalDto> getAnimalsByOwner(UUID ownerId) {
        return animalRepository.findByOwnerId(ownerId).stream()
                .map(this::mapToDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AnimalDto> getAnimalsByType(AnimalType type) {
        return animalRepository.findByType(type.name()).stream()
                .map(this::mapToDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AnimalDto> getSickAnimals() {
        return animalRepository.findByStatus(AnimalStatus.SICK.name()).stream()
                .map(this::mapToDto).collect(Collectors.toList());
    }

    private void validateAnimal(Animal animal) {
        if (animalRepository.findByTagId(animal.getTagId())
                .filter(a -> !a.getId().equals(animal.getId())).isPresent()) {
            throw new IllegalArgumentException("Tag ID already in use");
        }
        if (animal.getParent() != null) {
            Animal parent = animalRepository.findById(animal.getParent().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Parent not found"));
            if (parent.getGender() != Gender.FEMALE) {
                throw new IllegalArgumentException("Only females can be parents");
            }
        }
        if (animal.getStatus() == AnimalStatus.SOLD &&
                (animal.getSalePrice() == null || animal.getSalePrice().compareTo(BigDecimal.ZERO) <= 0)) {
            throw new IllegalArgumentException("Sold animal requires sale price");
        }
    }

    private void updateFields(Animal target, AnimalDto source) {
        target.setTagId(source.getTagId());
        target.setType(source.getType());
        target.setBreed(source.getBreed());
        target.setGender(source.getGender());
        target.setDateOfBirth(source.getDateOfBirth());
        if (source.getOwnerId() != null) {
            target.setOwner(ownerRepository.findById(source.getOwnerId())
                    .orElseThrow(() -> new RuntimeException("Owner not found")));
        }
        if (source.getParentId() != null) {
            target.setParent(animalRepository.findById(source.getParentId())
                    .orElseThrow(() -> new RuntimeException("Parent not found")));
        }
        target.setStatus(source.getStatus());
        target.setMilkProduction(source.getMilkProduction());
        target.setPhoto(source.getPhoto());
        target.setSalePrice(source.getSalePrice());
        target.setNotes(source.getNotes());
    }

    private Animal mapToEntity(AnimalDto dto) {
        Animal a = new Animal();
        a.setId(dto.getId());
        a.setTagId(dto.getTagId());
        a.setType(dto.getType());
        a.setBreed(dto.getBreed());
        a.setGender(dto.getGender());
        a.setDateOfBirth(dto.getDateOfBirth());
        if (dto.getOwnerId() != null) {
            a.setOwner(ownerRepository.findById(dto.getOwnerId()).orElse(null));
        }
        if (dto.getParentId() != null) {
            a.setParent(animalRepository.findById(dto.getParentId()).orElse(null));
        }
        a.setStatus(dto.getStatus());
        a.setMilkProduction(dto.getMilkProduction());
        a.setPhoto(dto.getPhoto());
        a.setSalePrice(dto.getSalePrice());
        a.setNotes(dto.getNotes());
        return a;
    }

    private AnimalDto mapToDto(Animal a) {
        AnimalDto dto = new AnimalDto();
        dto.setId(a.getId());
        dto.setTagId(a.getTagId());
        dto.setType(a.getType());
        dto.setBreed(a.getBreed());
        dto.setGender(a.getGender());
        dto.setDateOfBirth(a.getDateOfBirth());
        dto.setOwnerId(a.getOwner() != null ? a.getOwner().getId() : null);
        dto.setOwnerName(a.getOwner() != null ? a.getOwner().getName() : null);
        dto.setParentId(a.getParent() != null ? a.getParent().getId() : null);
        dto.setParentTagId(a.getParent() != null ? a.getParent().getTagId() : null);
        dto.setStatus(a.getStatus());
        dto.setMilkProduction(a.getMilkProduction());
        dto.setPhoto(a.getPhoto());
        dto.setSalePrice(a.getSalePrice());
        dto.setNotes(a.getNotes());
        dto.setCreatedAt(a.getCreatedAt());
        dto.setUpdatedAt(a.getUpdatedAt());
        return dto;
    }
}