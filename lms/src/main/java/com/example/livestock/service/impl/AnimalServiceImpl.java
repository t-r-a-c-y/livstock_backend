package com.example.livestock.service.impl;

import com.example.livestock.dto.AnimalRequest;
import com.example.livestock.dto.AnimalResponse;
import com.example.livestock.entity.Animal;
import com.example.livestock.enums.AnimalStatus;
import com.example.livestock.enums.NotificationType;
import com.example.livestock.exception.BadRequestException;
import com.example.livestock.exception.ResourceNotFoundException;
import com.example.livestock.mapper.DtoMapper;
import com.example.livestock.repository.AnimalRepository;
import com.example.livestock.repository.OwnerRepository;
import com.example.livestock.security.CurrentUserService;
import com.example.livestock.service.AuditLogService;
import com.example.livestock.service.AnimalService;
import com.example.livestock.service.NotificationService;
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
    private final NotificationService notificationService;
    private final AuditLogService auditLogService;

    @Override
    public AnimalResponse create(AnimalRequest request) {
        if (animalRepository.existsByTagNumber(request.tagNumber())) {
            throw new BadRequestException("Animal tag number already exists");
        }
        Animal animal = new Animal();
        apply(animal, request);
        Animal saved = animalRepository.save(animal);
        notificationService.notify(saved.getOwner().getUser(), "Animal added",
                saved.getTagNumber() + " was added to your livestock records.", NotificationType.ANIMAL_ADDED);
        auditLogService.record("CREATE_ANIMAL", "Animal", saved.getId(), currentUserService.getCurrentUser(),
                saved.getTagNumber() + " was added");
        return DtoMapper.toAnimal(saved);
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
        AnimalStatus oldStatus = animal.getAnimalStatus();
        apply(animal, request);
        if (oldStatus != animal.getAnimalStatus()) {
            notificationService.notify(animal.getOwner().getUser(), "Animal status updated",
                    animal.getTagNumber() + " is now marked as " + animal.getAnimalStatus(), NotificationType.ANIMAL_STATUS_UPDATE);
        }
        auditLogService.record("UPDATE_ANIMAL", "Animal", animal.getId(), currentUserService.getCurrentUser(),
                animal.getTagNumber() + " was updated");
        return DtoMapper.toAnimal(animal);
    }

    @Override
    public void deactivate(Long id) {
        Animal animal = animalRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Animal not found"));
        animal.setActive(false);
        auditLogService.record("INACTIVATE_ANIMAL", "Animal", animal.getId(), currentUserService.getCurrentUser(),
                animal.getTagNumber() + " was inactivated");
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
