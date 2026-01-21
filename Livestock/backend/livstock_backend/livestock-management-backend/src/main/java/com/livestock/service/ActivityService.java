// ActivityService.java
package com.livestock.service;

import com.livestock.dto.ActivityDto;
import com.livestock.entity.Activity;
import com.livestock.entity.Animal;
import com.livestock.entity.FinancialRecord;
import com.livestock.entity.User;
import com.livestock.entity.enums.ActivityType;
import com.livestock.entity.enums.FinancialType;
import com.livestock.repository.ActivityRepository;
import com.livestock.repository.AnimalRepository;
import com.livestock.repository.FinancialRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ActivityService {

    private final ActivityRepository activityRepository;
    private final AnimalRepository animalRepository;
    private final FinancialRecordRepository financialRecordRepository;
    private final NotificationService notificationService;
    private final UserService userService;

    @Transactional
    public ActivityDto createActivity(ActivityDto dto, UUID currentUserId) {
        Activity activity = new Activity();
        activity.setType(dto.getType());
        activity.setDescription(dto.getDescription());
        activity.setDate(dto.getDate());
        activity.setAmount(dto.getAmount());
        activity.setCost(dto.getCost());
        activity.setNotes(dto.getNotes());
        activity.setCreatedBy(userService.getUserEntity(currentUserId));

        Set<Animal> animals = dto.getAnimalIds().stream()
                .map(animalRepository::findById)
                .filter(java.util.Optional::isPresent)
                .map(java.util.Optional::get)
                .collect(Collectors.toSet());
        activity.setAnimals(animals);

        Activity saved = activityRepository.save(activity);

        createFinancialRecords(saved, currentUserId);

        if (saved.getType() == ActivityType.BIRTH) {
            handleBirth(saved);
        }

        return mapToDto(saved);
    }

    @Transactional
    public ActivityDto updateActivity(UUID id, ActivityDto dto, UUID currentUserId) {
        Activity existing = activityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Activity not found"));

        existing.setType(dto.getType());
        existing.setDescription(dto.getDescription());
        existing.setDate(dto.getDate());
        existing.setAmount(dto.getAmount());
        existing.setCost(dto.getCost());
        existing.setNotes(dto.getNotes());

        Set<Animal> animals = dto.getAnimalIds().stream()
                .map(animalRepository::findById)
                .filter(java.util.Optional::isPresent)
                .map(java.util.Optional::get)
                .collect(Collectors.toSet());
        existing.setAnimals(animals);

        Activity saved = activityRepository.save(existing);

        createFinancialRecords(saved, currentUserId);

        return mapToDto(saved);
    }

    @Transactional(readOnly = true)
    public ActivityDto getActivityById(UUID id) {
        return mapToDto(activityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Activity not found")));
    }

    @Transactional(readOnly = true)
    public List<ActivityDto> getAllActivities() {
        return activityRepository.findAll().stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Transactional
    public void deleteActivity(UUID id) {
        activityRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<ActivityDto> getActivitiesByAnimal(UUID animalId) {
        return activityRepository.findByAnimalId(animalId).stream()
                .map(this::mapToDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ActivityDto> getActivitiesByDateRange(LocalDate start, LocalDate end) {
        return activityRepository.findByDateBetween(start, end).stream()
                .map(this::mapToDto).collect(Collectors.toList());
    }

    private void createFinancialRecords(Activity activity, UUID currentUserId) {
        User creator = userService.getUserEntity(currentUserId);

        if (activity.getCost() != null && activity.getCost().compareTo(BigDecimal.ZERO) > 0) {
            FinancialRecord exp = new FinancialRecord();
            exp.setType(FinancialType.EXPENSE);
            exp.setCategory(activity.getType().name());
            exp.setAmount(activity.getCost());
            exp.setDescription("Cost from activity: " + activity.getDescription());
            exp.setDate(activity.getDate());
            exp.setCreatedBy(creator);
            if (!activity.getAnimals().isEmpty()) exp.setAnimal(activity.getAnimals().iterator().next());
            financialRecordRepository.save(exp);
        }

        if (activity.getAmount() != null && activity.getAmount().compareTo(BigDecimal.ZERO) > 0 &&
                (activity.getType() == ActivityType.SALE || activity.getType() == ActivityType.BIRTH)) {
            FinancialRecord inc = new FinancialRecord();
            inc.setType(FinancialType.INCOME);
            inc.setCategory(activity.getType().name());
            inc.setAmount(activity.getAmount());
            inc.setDescription("Income from activity: " + activity.getDescription());
            inc.setDate(activity.getDate());
            inc.setCreatedBy(creator);
            if (!activity.getAnimals().isEmpty()) inc.setAnimal(activity.getAnimals().iterator().next());
            financialRecordRepository.save(inc);
        }
    }

    private void handleBirth(Activity activity) {
        if (activity.getAnimals().size() != 1) return;
        Animal mother = activity.getAnimals().iterator().next();
        if (mother.getGender() != Gender.FEMALE) return;

        notificationService.createNotification(
                "Birth recorded",
                mother.getTagId() + " gave birth on " + activity.getDate(),
                NotificationType.BREEDING_REMINDER,
                NotificationCategory.SUCCESS,
                Priority.MEDIUM,
                "animal",
                mother.getId()
        );
    }

    private ActivityDto mapToDto(Activity a) {
        ActivityDto dto = new ActivityDto();
        dto.setId(a.getId());
        dto.setType(a.getType());
        dto.setDescription(a.getDescription());
        dto.setDate(a.getDate());
        dto.setAmount(a.getAmount());
        dto.setCost(a.getCost());
        dto.setNotes(a.getNotes());
        dto.setCreatedById(a.getCreatedBy().getId());
        dto.setCreatedByName(a.getCreatedBy().getFirstName() + " " + a.getCreatedBy().getLastName());
        dto.setCreatedAt(a.getCreatedAt());
        dto.setAnimalIds(a.getAnimals().stream().map(Animal::getId).collect(Collectors.toList()));
        return dto;
    }
}