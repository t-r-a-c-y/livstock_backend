// src/main/java/com/livestock/service/ActivityService.java
package com.livestock.service;

import com.livestock.dto.request.ActivityRequest;
import com.livestock.dto.response.ActivityResponse;
import com.livestock.entity.Activity;
import com.livestock.entity.Animal;
import com.livestock.entity.FinancialRecord;
import com.livestock.repository.ActivityRepository;
import com.livestock.repository.AnimalRepository;
import com.livestock.repository.FinancialRecordRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ActivityService {

    private final ActivityRepository activityRepository;
    private final AnimalRepository animalRepository;
    private final FinancialRecordRepository financialRecordRepository;
    private final ModelMapper modelMapper;

    @Transactional
    public ActivityResponse createActivity(ActivityRequest request) {
        List<Animal> animals = animalRepository.findAllById(request.getAnimalIds())
                .stream()
                .filter(a -> a.getDeletedAt() == null)
                .collect(Collectors.toList());

        if (animals.isEmpty()) {
            throw new ResourceNotFoundException("No valid animals found");
        }

        Activity activity = modelMapper.map(request, Activity.class);
        activity.setAnimals(animals);
        activity = activityRepository.save(activity);

        // Auto-create financial record if cost/amount exists
        if (request.getCost() != null && request.getCost().compareTo(java.math.BigDecimal.ZERO) > 0) {
            FinancialRecord fr = new FinancialRecord();
            fr.setType("expense");
            fr.setCategory("Activity: " + request.getType());
            fr.setAmount(request.getCost());
            fr.setDescription("Auto-generated from " + request.getType() + " activity");
            fr.setDate(LocalDateTime.now());
            fr.setCreatedBy(request.getCreatedBy());
            financialRecordRepository.save(fr);
        }

        return mapToResponse(activity);
    }

    public List<ActivityResponse> getAllActivities(String type, UUID animalId, LocalDateTime from, LocalDateTime to) {
        List<Activity> activities;
        if (animalId != null) {
            activities = activityRepository.findByAnimalId(animalId);
        } else if (from != null && to != null) {
            activities = activityRepository.findByDateRange(from, to);
        } else if (type != null) {
            activities = activityRepository.findByTypeAndDeletedAtIsNull(type);
        } else {
            activities = activityRepository.findAllActive();
        }
        return activities.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    private ActivityResponse mapToResponse(Activity activity) {
        ActivityResponse response = modelMapper.map(activity, ActivityResponse.class);
        response.setAnimalIds(activity.getAnimals().stream().map(Animal::getId).collect(Collectors.toList()));
        response.setAnimalTagIds(activity.getAnimals().stream().map(Animal::getTagId).collect(Collectors.toList()));
        return response;
    }

    // getById, update, delete — similar pattern
}