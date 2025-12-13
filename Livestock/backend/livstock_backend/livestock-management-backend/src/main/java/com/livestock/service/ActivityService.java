// src/main/java/com/livestock/service/ActivityService.java
package com.livestock.service;

import com.livestock.dto.request.ActivityRequest;
import com.livestock.dto.response.ActivityResponse;
import com.livestock.entity.Activity;
import com.livestock.entity.Animal;
import com.livestock.entity.FinancialRecord;
import com.livestock.exception.ResourceNotFoundException;
import com.livestock.repository.ActivityRepository;
import com.livestock.repository.AnimalRepository;
import com.livestock.repository.FinancialRecordRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ActivityService {

    private final ActivityRepository activityRepository;
    private final AnimalRepository animalRepository;
    private final FinancialRecordRepository financialRecordRepository;
    private final ModelMapper modelMapper;

    public ActivityResponse createActivity(ActivityRequest request) {
        List<Animal> animals = animalRepository.findAllById(request.getAnimalIds())
                .stream()
                .filter(a -> a.getDeletedAt() == null)
                .toList();

        if (animals.isEmpty()) {
            throw new ResourceNotFoundException("No valid animals found");
        }

        Activity activity = modelMapper.map(request, Activity.class);
        activity.setAnimals(animals);  // ← now works
        activity = activityRepository.save(activity);

        // Auto-create expense if cost exists
        if (request.getCost() != null && request.getCost().compareTo(BigDecimal.ZERO) > 0) {
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
        // ... (your filtering logic)
        List<Activity> activities = activityRepository.findAllActive(); // example
        return activities.stream()
                .map(this::mapToResponse)
                .toList();
    }

    private ActivityResponse mapToResponse(Activity activity) {
        ActivityResponse response = modelMapper.map(activity, ActivityResponse.class);
        response.setAnimalIds(activity.getAnimals().stream()
                .map(Animal::getId)
                .toList());
        response.setAnimalTagIds(activity.getAnimals().stream()
                .map(Animal::getTagId)
                .toList());
        return response;
    }
}