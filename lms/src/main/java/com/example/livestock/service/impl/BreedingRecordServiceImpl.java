package com.example.livestock.service.impl;

import com.example.livestock.dto.BreedingRecordRequest;
import com.example.livestock.dto.BreedingRecordResponse;
import com.example.livestock.entity.Animal;
import com.example.livestock.entity.BreedingRecord;
import com.example.livestock.enums.AnimalType;
import com.example.livestock.enums.NotificationType;
import com.example.livestock.exception.BadRequestException;
import com.example.livestock.exception.ResourceNotFoundException;
import com.example.livestock.mapper.DtoMapper;
import com.example.livestock.repository.AnimalRepository;
import com.example.livestock.repository.BreedingRecordRepository;
import com.example.livestock.repository.OwnerRepository;
import com.example.livestock.security.CurrentUserService;
import com.example.livestock.service.BreedingRecordService;
import com.example.livestock.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class BreedingRecordServiceImpl implements BreedingRecordService {
    private final BreedingRecordRepository breedingRecordRepository;
    private final AnimalRepository animalRepository;
    private final OwnerRepository ownerRepository;
    private final CurrentUserService currentUserService;
    private final NotificationService notificationService;

    @Override
    public BreedingRecordResponse create(BreedingRecordRequest request) {
        BreedingRecord record = new BreedingRecord();
        apply(record, request);
        BreedingRecord saved = breedingRecordRepository.save(record);
        notificationService.notify(saved.getCow().getOwner().getUser(), "Breeding record added",
                "A breeding record was added for " + saved.getCow().getTagNumber(), NotificationType.BREEDING_UPDATE);
        return DtoMapper.toBreeding(saved);
    }

    @Override
    public BreedingRecordResponse update(Long id, BreedingRecordRequest request) {
        BreedingRecord record = breedingRecordRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Breeding record not found"));
        apply(record, request);
        notificationService.notify(record.getCow().getOwner().getUser(), "Breeding record updated",
                "A breeding record was updated for " + record.getCow().getTagNumber(), NotificationType.BREEDING_UPDATE);
        return DtoMapper.toBreeding(record);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BreedingRecordResponse> findAll(Pageable pageable) {
        return breedingRecordRepository.findByActiveTrue(pageable).map(DtoMapper::toBreeding);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BreedingRecordResponse> findMine(Pageable pageable) {
        var owner = ownerRepository.findByUserEmailAndActiveTrue(currentUserService.getCurrentUser().getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Owner profile not found"));
        return breedingRecordRepository.findByCowOwnerIdAndActiveTrue(owner.getId(), pageable).map(DtoMapper::toBreeding);
    }

    private void apply(BreedingRecord record, BreedingRecordRequest request) {
        Animal cow = animalRepository.findByIdAndActiveTrue(request.cowId())
                .orElseThrow(() -> new ResourceNotFoundException("Cow not found"));
        if (cow.getAnimalType() != AnimalType.COW) {
            throw new BadRequestException("Breeding records can only be linked to cows");
        }
        record.setCow(cow);
        record.setMatingDate(request.matingDate());
        record.setMaleAnimalUsed(request.maleAnimalUsed());
        if (request.pregnancyStatus() != null) {
            record.setPregnancyStatus(request.pregnancyStatus());
        }
        record.setExpectedBirthDate(request.expectedBirthDate());
        record.setActualBirthDate(request.actualBirthDate());
        record.setNotes(request.notes());
    }
}
