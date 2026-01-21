package com.livestock.service;

import com.livestock.dto.ActivityDto;
import com.livestock.entity.Activity;
import com.livestock.entity.Animal;
import com.livestock.entity.FinancialRecord;
import com.livestock.entity.User;
import com.livestock.entity.enums.ActivityType;
import com.livestock.entity.enums.FinancialType;
import com.livestock.entity.enums.Gender;
import com.livestock.entity.enums.NotificationCategory;
import com.livestock.entity.enums.NotificationType;
import com.livestock.entity.enums.Priority;
import com.livestock.repository.ActivityRepository;
import com.livestock.repository.AnimalRepository;
import com.livestock.repository.FinancialRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
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

        User creator = userService.getUserEntity(currentUserId);
        activity.setCreatedBy(creator);

        if (dto.getAnimalIds() == null || dto.getAnimalIds().isEmpty()) {
            throw new IllegalArgumentException("At least one animal must be selected");
        }

        Set<Animal> animals = dto.getAnimalIds().stream()
                .map(id -> animalRepository.findById(id)
                        .orElseThrow(() -> new IllegalArgumentException("Animal not found: " + id)))
                .collect(Collectors.toSet());

        activity.setAnimals(animals);

        Activity saved = activityRepository.save(activity);

        createFinancialRecordsFromActivity(saved, creator);

        if (saved.getType() == ActivityType.BIRTH) {
            handleBirthActivity(saved);
        }

        return mapToDto(saved);
    }

    @Transactional
    public ActivityDto updateActivity(UUID id, ActivityDto dto, UUID currentUserId) {
        Activity existing = activityRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Activity not found"));

        existing.setType(dto.getType());
        existing.setDescription(dto.getDescription());
        existing.setDate(dto.getDate());
        existing.setAmount(dto.getAmount());
        existing.setCost(dto.getCost());
        existing.setNotes(dto.getNotes());

        if (dto.getAnimalIds() != null && !dto.getAnimalIds().isEmpty()) {
            Set<Animal> animals = dto.getAnimalIds().stream()
                    .map(animalId -> animalRepository.findById(animalId)
                            .orElseThrow(() -> new IllegalArgumentException("Animal not found")))
                    .collect(Collectors.toSet());
            existing.setAnimals(animals);
        }

        Activity saved = activityRepository.save(existing);

        createFinancialRecordsFromActivity(saved, userService.getUserEntity(currentUserId));

        return mapToDto(saved);
    }

    private void createFinancialRecordsFromActivity(Activity activity, User creator) {
        if (activity.getCost() != null && activity.getCost().compareTo(BigDecimal.ZERO) > 0) {
            FinancialRecord expense = new FinancialRecord();
            expense.setType(FinancialType.EXPENSE);
            expense.setCategory(activity.getType().name());
            expense.setAmount(activity.getCost());
            expense.setDescription("Cost from activity: " + activity.getDescription());
            expense.setDate(activity.getDate());
            expense.setCreatedBy(creator);
            if (!activity.getAnimals().isEmpty()) {
                expense.setAnimal(activity.getAnimals().iterator().next());
            }
            financialRecordRepository.save(expense);
        }

        if (activity.getAmount() != null && activity.getAmount().compareTo(BigDecimal.ZERO) > 0 &&
                (activity.getType() == ActivityType.SALE || activity.getType() == ActivityType.BIRTH)) {
            FinancialRecord income = new FinancialRecord();
            income.setType(FinancialType.INCOME);
            income.setCategory(activity.getType().name());
            income.setAmount(activity.getAmount());
            income.setDescription("Income from activity: " + activity.getDescription());
            income.setDate(activity.getDate());
            income.setCreatedBy(creator);
            if (!activity.getAnimals().isEmpty()) {
                income.setAnimal(activity.getAnimals().iterator().next());
            }
            financialRecordRepository.save(income);
        }
    }

    private void handleBirthActivity(Activity activity) {
        if (activity.getAnimals().size() != 1) {
            throw new IllegalStateException("Birth must have exactly one mother");
        }

        Animal mother = activity.getAnimals().iterator().next();
        if (mother.getGender() != Gender.FEMALE) {
            throw new IllegalStateException("Only female can give birth");
        }

        notificationService.createNotification(
                "Birth Recorded",
                mother.getTagId() + " gave birth on " + activity.getDate(),
                NotificationType.BREEDING_REMINDER,
                NotificationCategory.SUCCESS,
                Priority.MEDIUM,
                "animal",
                mother.getId()
        );
    }

    private ActivityDto mapToDto(Activity activity) {
        ActivityDto dto = new ActivityDto();
        dto.setId(activity.getId());
        dto.setType(activity.getType());
        dto.setDescription(activity.getDescription());
        dto.setDate(activity.getDate());
        dto.setAmount(activity.getAmount());
        dto.setCost(activity.getCost());
        dto.setNotes(activity.getNotes());
        dto.setCreatedById(activity.getCreatedBy().getId());
        dto.setCreatedByName(activity.getCreatedBy().getFirstName() + " " + activity.getCreatedBy().getLastName());
        dto.setCreatedAt(activity.getCreatedAt());
        dto.setAnimalIds(activity.getAnimals().stream().map(Animal::getId).collect(Collectors.toList()));
        return dto;
    }

    // ... other methods (getAllActivities, getById, delete, etc.) remain the same
}