package com.livestock.backend.service;

import com.livestock.backend.dto.ActivityDTO;
import com.livestock.backend.dto.FinancialRecordDTO;
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

import java.time.LocalDate;
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
    public Page<ActivityDTO> getAll(String type, LocalDate dateFrom, LocalDate dateTo, UUID animalId, Pageable pageable) {
        logger.info("Fetching activities with type: {}, dateFrom: {}, dateTo: {}, animalId: {}", type, dateFrom, dateTo, animalId);
        Specification<Activity> spec = (root, query, cb) -> cb.isNull(root.get("deletedAt"));
        if (type != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("type"), type));
        }
        if (dateFrom != null) {
            spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("date"), dateFrom));
        }
        if (dateTo != null) {
            spec = spec.and((root, query, cb) -> cb.lessThanOrEqualTo(root.get("date"), dateTo));
        }
        if (animalId != null) {
            spec = spec.and((root, query, cb) -> cb.isMember(animalId, root.get("animalIds")));
        }
        return activityRepository.findAll(spec, pageable).map(this::toDTO);
    }

    @Transactional(readOnly = true)
    public ActivityDTO getById(UUID id) {
        logger.info("Fetching activity with id: {}", id);
        Activity activity = activityRepository.findById(id)
                .filter(a -> a.getDeletedAt() == null)
                .orElseThrow(() -> new RuntimeException("Activity not found"));
        return toDTO(activity);
    }

    @Transactional
    public ActivityDTO create(ActivityDTO dto) {
        logger.info("Creating activity with type: {}", dto.getType());
        Activity activity = new Activity();
        updateEntityFromDTO(activity, dto);
        activity.setCreatedAt(LocalDateTime.now());
        activity = activityRepository.save(activity);

        if (dto.getAmount() != null || dto.getCost() != null) {
            FinancialRecordDTO financialRecordDTO = new FinancialRecordDTO();
            financialRecordDTO.setType(dto.getAmount() != null ? "income" : "expense");
            financialRecordDTO.setAmount(dto.getAmount() != null ? dto.getAmount() : dto.getCost());
            financialRecordDTO.setDescription(dto.getDescription());
            financialRecordDTO.setDate(dto.getDate());
            financialRecordDTO.setCreatedBy(dto.getCreatedBy());
            financialRecordDTO.setCreatedAt(LocalDateTime.now());
            financialRecordDTO.setUpdatedAt(LocalDateTime.now());
            financialRecordRepository.save(toFinancialRecordEntity(financialRecordDTO));
        }

        return toDTO(activity);
    }

    @Transactional
    public ActivityDTO update(UUID id, ActivityDTO dto) {
        logger.info("Updating activity with id: {}", id);
        Activity activity = activityRepository.findById(id)
                .filter(a -> a.getDeletedAt() == null)
                .orElseThrow(() -> new RuntimeException("Activity not found"));
        updateEntityFromDTO(activity, dto);
        activity = activityRepository.save(activity);
        return toDTO(activity);
    }

    @Transactional
    public void delete(UUID id) {
        logger.info("Soft deleting activity with id: {}", id);
        Activity activity = activityRepository.findById(id)
                .filter(a -> a.getDeletedAt() == null)
                .orElseThrow(() -> new RuntimeException("Activity not found"));
        activity.setDeletedAt(LocalDateTime.now());
        activityRepository.save(activity);
    }

    private ActivityDTO toDTO(Activity activity) {
        ActivityDTO dto = new ActivityDTO();
        dto.setId(activity.getId());
        dto.setAnimalIds(activity.getAnimalIds());
        dto.setType(activity.getType());
        dto.setDescription(activity.getDescription());
        dto.setDate(activity.getDate());
        dto.setAmount(activity.getAmount());
        dto.setCost(activity.getCost());
        dto.setNotes(activity.getNotes());
        dto.setCreatedBy(activity.getCreatedBy());
        dto.setCreatedAt(activity.getCreatedAt());
        dto.setDeletedAt(activity.getDeletedAt());
        return dto;
    }

    private void updateEntityFromDTO(Activity activity, ActivityDTO dto) {
        activity.setAnimalIds(dto.getAnimalIds());
        activity.setType(dto.getType());
        activity.setDescription(dto.getDescription());
        activity.setDate(dto.getDate());
        activity.setAmount(dto.getAmount());
        activity.setCost(dto.getCost());
        activity.setNotes(dto.getNotes());
        activity.setCreatedBy(dto.getCreatedBy());
    }

    private FinancialRecord toFinancialRecordEntity(FinancialRecordDTO dto) {
        FinancialRecord entity = new FinancialRecord();
        entity.setType(dto.getType());
        entity.setCategory(dto.getCategory());
        entity.setAmount(dto.getAmount());
        entity.setDescription(dto.getDescription());
        entity.setDate(dto.getDate());
        entity.setOwnerId(dto.getOwnerId());
        entity.setAnimalId(dto.getAnimalId());
        entity.setPaymentMethod(dto.getPaymentMethod());
        entity.setReceiptNumber(dto.getReceiptNumber());
        entity.setReceiptImage(dto.getReceiptImage());
        entity.setCreatedBy(dto.getCreatedBy());
        entity.setCreatedAt(dto.getCreatedAt());
        entity.setUpdatedAt(dto.getUpdatedAt());
        return entity;
    }
}