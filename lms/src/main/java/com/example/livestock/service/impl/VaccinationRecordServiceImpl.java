package com.example.livestock.service.impl;

import com.example.livestock.dto.VaccinationRecordRequest;
import com.example.livestock.dto.VaccinationRecordResponse;
import com.example.livestock.entity.VaccinationRecord;
import com.example.livestock.enums.NotificationType;
import com.example.livestock.exception.ResourceNotFoundException;
import com.example.livestock.mapper.DtoMapper;
import com.example.livestock.repository.AnimalRepository;
import com.example.livestock.repository.OwnerRepository;
import com.example.livestock.repository.VaccinationRecordRepository;
import com.example.livestock.security.CurrentUserService;
import com.example.livestock.service.NotificationService;
import com.example.livestock.service.VaccinationRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Transactional
public class VaccinationRecordServiceImpl implements VaccinationRecordService {
    private final VaccinationRecordRepository vaccinationRecordRepository;
    private final AnimalRepository animalRepository;
    private final OwnerRepository ownerRepository;
    private final CurrentUserService currentUserService;
    private final NotificationService notificationService;

    @Override
    public VaccinationRecordResponse create(VaccinationRecordRequest request) {
        var animal = animalRepository.findByIdAndActiveTrue(request.animalId())
                .orElseThrow(() -> new ResourceNotFoundException("Animal not found"));
        VaccinationRecord record = new VaccinationRecord();
        record.setAnimal(animal);
        record.setVaccineName(request.vaccineName());
        record.setVaccinationDate(request.vaccinationDate());
        record.setNextDueDate(request.nextDueDate());
        record.setAdministeredBy(request.administeredBy());
        record.setNotes(request.notes());
        VaccinationRecord saved = vaccinationRecordRepository.save(record);
        notificationService.notify(animal.getOwner().getUser(), "Vaccination record added",
                "Vaccination information was added for " + animal.getTagNumber(), NotificationType.HEALTH_UPDATE);
        return DtoMapper.toVaccination(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<VaccinationRecordResponse> findAll(Pageable pageable) {
        return vaccinationRecordRepository.findByActiveTrue(pageable).map(DtoMapper::toVaccination);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<VaccinationRecordResponse> findMine(Pageable pageable) {
        var owner = ownerRepository.findByUserEmailAndActiveTrue(currentUserService.getCurrentUser().getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Owner profile not found"));
        return vaccinationRecordRepository.findByAnimalOwnerIdAndActiveTrue(owner.getId(), pageable).map(DtoMapper::toVaccination);
    }

    @Override
    @Scheduled(cron = "0 0 7 * * *")
    public void createDueSoonNotifications() {
        LocalDate today = LocalDate.now();
        vaccinationRecordRepository.findByNextDueDateBetweenAndActiveTrue(today, today.plusDays(7))
                .forEach(record -> notificationService.notify(record.getAnimal().getOwner().getUser(), "Vaccination due soon",
                        record.getVaccineName() + " is due for " + record.getAnimal().getTagNumber(),
                        NotificationType.VACCINATION_DUE));
    }
}
