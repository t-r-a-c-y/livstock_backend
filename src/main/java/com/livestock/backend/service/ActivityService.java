package com.livestock.backend.service;

import com.livestock.backend.dto.ActivityDTO;
import com.livestock.backend.model.Activity;
import com.livestock.backend.model.FinancialRecord;
import com.livestock.backend.repository.ActivityRepository;
import com.livestock.backend.repository.FinancialRecordRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class ActivityService {
    private static final Logger logger = LoggerFactory.getLogger(ActivityService.class);

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private FinancialRecordRepository financialRecordRepository;

    @Transactional(readOnly = true)
    public Page<ActivityDTO> getAll(String type, LocalDateTime startDate, LocalDateTime endDate, UUID animalId, Pageable pageable) {
        logger.info("Fetching activities with filters");
        Specification<Activity> spec = Specification.where((root, query, cb) -> cb.isNull(root.get("deletedAt")));
        if (type != null) spec = spec.and((root, query, cb) -> cb.equal(root.get("type"), type));
        if (animalId != null) spec = spec.and((root, query, cb) -> cb.equal(root.get("animalId"), animalId));
        if (startDate != null) spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("date"), startDate));
        if (endDate != null) spec = spec.and((root, query, cb) -> cb.lessThanOrEqualTo(root.get("date"), endDate));
        return activityRepository.findAll(spec, pageable).map(this::toDTO);
    }

    @Transactional(readOnly = true)
    public ActivityDTO getById(UUID id) {
        logger.info("Fetching activity by ID: {}", id);
        Activity activity = activityRepository.findById(id).orElseThrow(() -> new RuntimeException("Activity not found"));
        if (activity.getDeletedAt() != null) throw new RuntimeException("Activity deleted");
        return toDTO(activity);
    }

    @Transactional
    public ActivityDTO create(ActivityDTO dto) {
        logger.info("Creating activity: {}", dto.getType());
        Activity activity = new Activity();
        mapToEntity(dto, activity);
        activity.setCreatedAt(LocalDateTime.now());
        activity.setUpdatedAt(LocalDateTime.now());
        activity = activityRepository.save(activity);

        // Auto-create financial record
        if (dto.getCost() != null && dto.getCost().compareTo(BigDecimal.ZERO) > 0) {
            FinancialRecord fr = new FinancialRecord();
            fr.setType("expense");
            fr.setAmount(dto.getCost());
            fr.setDate(activity.getDate().toLocalDate());
            fr.setDescription("Cost for activity " + activity.getId());
            fr.setActivityId(activity.getId());
            fr.setCreatedBy(getCurrentUserId());
            fr.setCreatedAt(LocalDateTime.now());
            fr.setUpdatedAt(LocalDateTime.now());
            financialRecordRepository.save(fr);
        }
        if (dto.getAmount() != null && dto.getAmount().compareTo(BigDecimal.ZERO) > 0) {
            FinancialRecord fr = new FinancialRecord();
            fr.setType("income");
            fr.setAmount(dto.getAmount());
            fr.setDate(activity.getDate().toLocalDate());
            fr.setDescription("Income from activity " + activity.getId());
            fr.setActivityId(activity.getId());
            fr.setCreatedBy(getCurrentUserId());
            fr.setCreatedAt(LocalDateTime.now());
            fr.setUpdatedAt(LocalDateTime.now());
            financialRecordRepository.save(fr);
        }

        return toDTO(activity);
    }

    @Transactional
    public ActivityDTO update(UUID id, ActivityDTO dto) {
        logger.info("Updating activity: {}", id);
        Activity activity = activityRepository.findById(id).orElseThrow(() -> new RuntimeException("Activity not found"));
        if (activity.getDeletedAt() != null) throw new RuntimeException("Activity deleted");
        mapToEntity(dto, activity);
        activity.setUpdatedAt(LocalDateTime.now());
        activity = activityRepository.save(activity);
        // Note: No auto-update for financial, assume manual
        return toDTO(activity);
    }

    @Transactional
    public void softDelete(UUID id) {
        logger.info("Soft deleting activity: {}", id);
        Activity activity = activityRepository.findById(id).orElseThrow(() -> new RuntimeException("Activity not found"));
        activity.setDeletedAt(LocalDateTime.now());
        activityRepository.save(activity);
    }

    private ActivityDTO toDTO(Activity activity) {
        ActivityDTO dto = new ActivityDTO();
        dto.setId(activity.getId());
        dto.setType(activity.getType());
        dto.setDate(activity.getDate());
        dto.setAnimalId(activity.getAnimalId());
        dto.setDescription(activity.getDescription());
        dto.setCost(activity.getCost());
        dto.setAmount(activity.getAmount());
        dto.setCreatedAt(activity.getCreatedAt());
        dto.setUpdatedAt(activity.getUpdatedAt());
        return dto;
    }

    private void mapToEntity(ActivityDTO dto, Activity activity) {
        activity.setType(dto.getType());
        activity.setDate(dto.getDate());
        activity.setAnimalId(dto.getAnimalId());
        activity.setDescription(dto.getDescription());
        activity.setCost(dto.getCost());
        activity.setAmount(dto.getAmount());
    }

    private UUID getCurrentUserId() {
        // same as in AnimalService
        // ...
    }
}