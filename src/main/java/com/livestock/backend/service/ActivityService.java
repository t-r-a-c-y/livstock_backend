package com.livestock.backend.service;

import com.livestock.backend.dto.ActivityDTO;
import com.livestock.backend.dto.ActivityCreateDTO;
import com.livestock.backend.dto.ActivityUpdateDTO;
import com.livestock.backend.dto.FinancialRecordCreateDTO;
import com.livestock.backend.model.Activity;
import com.livestock.backend.model.FinancialRecord;
import com.livestock.backend.repository.ActivityRepository;
import com.livestock.backend.repository.FinancialRecordRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ActivityService {
    private static final Logger logger = LoggerFactory.getLogger(ActivityService.class);
    private final ActivityRepository activityRepository;
    private final FinancialRecordRepository financialRecordRepository;
    private final ModelMapper modelMapper;

    public Page<ActivityDTO> getAllActivities(String type, LocalDate startDate, LocalDate endDate, UUID animalId, Pageable pageable) {
        logger.info("Fetching activities with filters: type={}, startDate={}, endDate={}, animalId={}", type, startDate, endDate, animalId);
        return activityRepository.findByFilters(type, startDate, endDate, animalId, pageable)
                .map(activity -> modelMapper.map(activity, ActivityDTO.class));
    }

    public ActivityDTO getActivityById(UUID id) {
        logger.info("Fetching activity with id: {}", id);
        Activity activity = activityRepository.findById(id)
                .filter(a -> a.getDeletedAt() == null)
                .orElseThrow(() -> new RuntimeException("Activity not found or deleted"));
        return modelMapper.map(activity, ActivityDTO.class);
    }

    @Transactional
    public ActivityDTO createActivity(ActivityCreateDTO dto) {
        logger.info("Creating new activity of type: {}", dto.getType());
        Activity activity = modelMapper.map(dto, Activity.class);
        activity.setCreatedAt(LocalDateTime.now());
        activity = activityRepository.save(activity);

        // Auto-create financial record if amount or cost is present
        if (dto.getAmount() != null || dto.getCost() != null) {
            FinancialRecordCreateDTO frDto = new FinancialRecordCreateDTO();
            frDto.setType(dto.getCost() != null ? "expense" : "income");
            frDto.setAmount(dto.getAmount() != null ? dto.getAmount() : dto.getCost());
            frDto.setDescription(dto.getDescription());
            frDto.setDate(dto.getDate());
            frDto.setAnimalId(dto.getAnimalIds() != null && dto.getAnimalIds().length > 0 ? dto.getAnimalIds()[0] : null);
            frDto.setCreatedBy(dto.getCreatedBy());
            FinancialRecord fr = modelMapper.map(frDto, FinancialRecord.class);
            fr.setCreatedAt(LocalDateTime.now());
            fr.setUpdatedAt(LocalDateTime.now());
            financialRecordRepository.save(fr);
        }

        return modelMapper.map(activity, ActivityDTO.class);
    }

    @Transactional
    public ActivityDTO updateActivity(UUID id, ActivityUpdateDTO dto) {
        logger.info("Updating activity with id: {}", id);
        Activity activity = activityRepository.findById(id)
                .filter(a -> a.getDeletedAt() == null)
                .orElseThrow(() -> new RuntimeException("Activity not found or deleted"));
        modelMapper.map(dto, activity);
        activity.setCreatedAt(LocalDateTime.now());
        activity = activityRepository.save(activity);
        return modelMapper.map(activity, ActivityDTO.class);
    }

    @Transactional
    public void softDeleteActivity(UUID id) {
        logger.info("Soft deleting activity with id: {}", id);
        Activity activity = activityRepository.findById(id)
                .filter(a -> a.getDeletedAt() == null)
                .orElseThrow(() -> new RuntimeException("Activity not found or already deleted"));
        activity.setDeletedAt(LocalDateTime.now());
        activityRepository.save(activity);
    }
}