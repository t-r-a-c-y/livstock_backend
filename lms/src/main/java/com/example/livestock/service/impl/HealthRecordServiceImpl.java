package com.example.livestock.service.impl;

import com.example.livestock.dto.HealthRecordRequest;
import com.example.livestock.dto.HealthRecordResponse;
import com.example.livestock.entity.HealthRecord;
import com.example.livestock.enums.NotificationType;
import com.example.livestock.exception.ResourceNotFoundException;
import com.example.livestock.mapper.DtoMapper;
import com.example.livestock.repository.AnimalRepository;
import com.example.livestock.repository.HealthRecordRepository;
import com.example.livestock.repository.OwnerRepository;
import com.example.livestock.security.CurrentUserService;
import com.example.livestock.service.AuditLogService;
import com.example.livestock.service.HealthRecordService;
import com.example.livestock.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class HealthRecordServiceImpl implements HealthRecordService {
    private final HealthRecordRepository healthRecordRepository;
    private final AnimalRepository animalRepository;
    private final OwnerRepository ownerRepository;
    private final CurrentUserService currentUserService;
    private final NotificationService notificationService;
    private final AuditLogService auditLogService;

    @Override
    public HealthRecordResponse create(HealthRecordRequest request) {
        var animal = animalRepository.findByIdAndActiveTrue(request.animalId())
                .orElseThrow(() -> new ResourceNotFoundException("Animal not found"));
        HealthRecord record = new HealthRecord();
        record.setAnimal(animal);
        record.setDiagnosis(request.diagnosis());
        record.setTreatment(request.treatment());
        record.setMedication(request.medication());
        record.setVeterinarianName(request.veterinarianName());
        record.setVisitDate(request.visitDate());
        record.setNextVisitDate(request.nextVisitDate());
        record.setNotes(request.notes());
        HealthRecord saved = healthRecordRepository.save(record);
        notificationService.notify(animal.getOwner().getUser(), "Health record added",
                "A health record was added for " + animal.getTagNumber(), NotificationType.HEALTH_UPDATE);
        auditLogService.record("CREATE_HEALTH_RECORD", "HealthRecord", saved.getId(), currentUserService.getCurrentUser(),
                "Health record added for " + animal.getTagNumber());
        return DtoMapper.toHealth(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<HealthRecordResponse> findAll(Pageable pageable) {
        return healthRecordRepository.findByActiveTrue(pageable).map(DtoMapper::toHealth);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<HealthRecordResponse> findMine(Pageable pageable) {
        var owner = ownerRepository.findByUserEmailAndActiveTrue(currentUserService.getCurrentUser().getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Owner profile not found"));
        return healthRecordRepository.findByAnimalOwnerIdAndActiveTrue(owner.getId(), pageable).map(DtoMapper::toHealth);
    }
}
