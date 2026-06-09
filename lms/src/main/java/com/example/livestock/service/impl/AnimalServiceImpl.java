package com.example.livestock.service.impl;

import com.example.livestock.dto.AnimalRequest;
import com.example.livestock.dto.AnimalResponse;
import com.example.livestock.entity.Animal;
import com.example.livestock.exception.BadRequestException;
import com.example.livestock.exception.ResourceNotFoundException;
import com.example.livestock.mapper.DtoMapper;
import com.example.livestock.repository.AnimalRepository;
import com.example.livestock.repository.OwnerRepository;
import com.example.livestock.security.CurrentUserService;
import com.example.livestock.service.AnimalService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AnimalServiceImpl implements AnimalService {
    private final AnimalRepository animalRepository;
    private final OwnerRepository ownerRepository;
    private final CurrentUserService currentUserService;

    @Override
    public AnimalResponse create(AnimalRequest request) {
        if (animalRepository.existsByTagNumber(request.tagNumber())) {
            throw new BadRequestException("Animal tag number already exists");
        }
        Animal animal = new Animal();
        apply(animal, request);
        return DtoMapper.toAnimal(animalRepository.save(animal));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AnimalResponse> findAll(boolean includeInactive, Pageable pageable) {
        return (includeInactive ? animalRepository.findAll(pageable) : animalRepository.findByActiveTrue(pageable))
                .map(DtoMapper::toAnimal);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AnimalResponse> findMine(Pageable pageable) {
        var owner = ownerRepository.findByUserEmailAndActiveTrue(currentUserService.getCurrentUser().getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Owner profile not found"));
        return animalRepository.findByOwnerIdAndActiveTrue(owner.getId(), pageable).map(DtoMapper::toAnimal);
    }

    @Override
    public AnimalResponse update(Long id, AnimalRequest request) {
        Animal animal = animalRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Animal not found"));
        apply(animal, request);
        return DtoMapper.toAnimal(animal);
    }

    @Override
    public void deactivate(Long id) {
        Animal animal = animalRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Animal not found"));
        animal.setActive(false);
    }

    private void apply(Animal animal, AnimalRequest request) {
        animal.setOwner(ownerRepository.findByIdAndActiveTrue(request.ownerId())
                .orElseThrow(() -> new ResourceNotFoundException("Owner not found")));
        animal.setTagNumber(request.tagNumber());
        animal.setAnimalType(request.animalType());
        animal.setBreed(request.breed());
        animal.setGender(request.gender());
        animal.setDateOfBirth(request.dateOfBirth());
        animal.setColor(request.color());
        animal.setWeight(request.weight());
        if (request.animalStatus() != null) {
            animal.setAnimalStatus(request.animalStatus());
        }
    }
}
