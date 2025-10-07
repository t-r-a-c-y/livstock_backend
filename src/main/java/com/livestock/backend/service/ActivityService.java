package com.livestock.backend.service;


import com.livestock.backend.dto.request.ActivityRequest;
import com.livestock.backend.dto.response.ActivityResponse;
import com.livestock.backend.exception.ResourceNotFoundException;
import com.livestock.backend.model.Activity;
import com.livestock.backend.model.Animal;
import com.livestock.backend.repository.ActivityRepository;
import com.livestock.backend.repository.AnimalRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ActivityService {

    private final ActivityRepository activityRepository;
    private final AnimalRepository animalRepository;
    private final ActivityMapper activityMapper;
    private final AuthService authService;

    public ActivityService(ActivityRepository activityRepository, AnimalRepository animalRepository, ActivityMapper activityMapper, AuthService authService) {
        this.activityRepository = activityRepository;
        this.animalRepository = animalRepository;
        this.activityMapper = activityMapper;
        this.authService = authService;
    }

    public Page<ActivityResponse> getAllActivities(int page, int size, String type, Date dateFrom, Date dateTo, UUID animalId) {
        Pageable pageable = PageRequest.of(page, size);
        Specification<Activity> spec = Specification.where(null);
        if (type != null) spec = spec.and((root, query, cb) -> cb.equal(root.get("type"), type));
        if (dateFrom != null) spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("date"), dateFrom));
        if (dateTo != null) spec = spec.and((root, query, cb) -> cb.lessThanOrEqualTo(root.get("date"), dateTo));
        if (animalId != null) spec = spec.and((root, query, cb) -> cb.isMember(animalRepository.getReferenceById(animalId), root.get("animals")));
        return activityRepository.findAll(spec, pageable).map(activityMapper::toResponse);
    }

    public ActivityResponse getActivityById(UUID id) {
        Activity activity = activityRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Activity not found"));
        return activityMapper.toResponse(activity);
    }

    public ActivityResponse createActivity(ActivityRequest request) {
        Activity activity = activityMapper.toEntity(request);
        if (request.getAnimalIds() != null) {
            List<Animal> animals = request.getAnimalIds().stream()
                    .map(animalRepository::findById)
                    .filter(java.util.Optional::isPresent)
                    .map(java.util.Optional::get)
                    .collect(Collectors.toList());
            activity.setAnimals(animals);
        }
        activity.setCreatedBy(authService.getCurrentUser());
        activity.setCreatedAt(new Date());
        activity = activityRepository.save(activity);
        return activityMapper.toResponse(activity);
    }

    public ActivityResponse updateActivity(UUID id, ActivityRequest request) {
        Activity activity = activityRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Activity not found"));
        activity.setType(request.getType());
        activity.setDescription(request.getDescription());
        activity.setDate(request.getDate());
        activity.setAmount(request.getAmount());
        activity.setCost(request.getCost());
        activity.setNotes(request.getNotes());
        if (request.getAnimalIds() != null) {
            List<Animal> animals = request.getAnimalIds().stream()
                    .map(animalRepository::findById)
                    .filter(java.util.Optional::isPresent)
                    .map(java.util.Optional::get)
                    .collect(Collectors.toList());
            activity.setAnimals(animals);
        }
        activity = activityRepository.save(activity);
        return activityMapper.toResponse(activity);
    }

    public void deleteActivity(UUID id) {
        activityRepository.deleteById(id);
    }
}
