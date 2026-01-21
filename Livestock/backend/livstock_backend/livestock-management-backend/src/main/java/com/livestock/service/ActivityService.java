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
import java.time.LocalDate;
import java.util.HashSet;
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

    // CREATE new activity
    @Transactional
    public ActivityDto createActivity(ActivityDto dto, UUID currentUserId) {
        Activity activity = new Activity();
        activity.setType(dto.getType());
        activity.setDescription(dto.getDescription());
        activity.setDate(dto.getDate());
        activity.setAmount(dto.getAmount());
        activity.setCost(dto.getCost());
        activity.setNotes(dto.getNotes());

        // Set creator
        User creator = userService.getUserEntity(currentUserId);
        activity.setCreatedBy(creator);

        // Validate and set animals
        if (dto.getAnimalIds() == null || dto.getAnimalIds().isEmpty()) {
            throw new IllegalArgumentException("At least one animal must be associated with the activity");
        }

        Set<Animal> animals = dto.getAnimalIds().stream()
                .map(id -> animalRepository.findById(id)
                        .orElseThrow(() -> new IllegalArgumentException("Animal not found: " + id)))
                .collect(Collectors.toSet());

        activity.setAnimals(animals);

        // Save
        Activity savedActivity = activityRepository.save(activity);

        // Auto-create financial records
        createFinancialRecordsFromActivity(savedActivity, creator);

        // Handle special case: birth activity
        if (savedActivity.getType() == ActivityType.BIRTH) {
            handleBirthActivity(savedActivity);
        }

        return mapToDto(savedActivity);
    }

    // UPDATE existing activity
    @Transactional
    public ActivityDto updateActivity(UUID id, ActivityDto dto, UUID currentUserId) {
        Activity existing = activityRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Activity not found"));

        // Update basic fields
        existing.setType(dto.getType());
        existing.setDescription(dto.getDescription());
        existing.setDate(dto.getDate());
        existing.setAmount(dto.getAmount());
        existing.setCost(dto.getCost());
        existing.setNotes(dto.getNotes());

        // Update animals if provided
        if (dto.getAnimalIds() != null && !dto.getAnimalIds().isEmpty()) {
            Set<Animal> animals = dto.getAnimalIds().stream()
                    .map(animalId -> animalRepository.findById(animalId)
                            .orElseThrow(() -> new IllegalArgumentException("Animal not found")))
                    .collect(Collectors.toSet());
            existing.setAnimals(animals);
        }

        Activity updatedActivity = activityRepository.save(existing);

        // Re-apply financial logic in case cost/amount changed
        createFinancialRecordsFromActivity(updatedActivity, userService.getUserEntity(currentUserId));

        return mapToDto(updatedActivity);
    }

    // GET single activity
    @Transactional(readOnly = true)
    public ActivityDto getActivityById(UUID id) {
        Activity activity = activityRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Activity not found"));
        return mapToDto(activity);
    }

    // GET all activities
    @Transactional(readOnly = true)
    public List<ActivityDto> getAllActivities() {
        return activityRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    // GET activities by animal
    @Transactional(readOnly = true)
    public List<ActivityDto> getActivitiesByAnimal(UUID animalId) {
        return activityRepository.findByAnimalId(animalId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    // GET activities in date range
    @Transactional(readOnly = true)
    public List<ActivityDto> getActivitiesByDateRange(LocalDate start, LocalDate end) {
        return activityRepository.findByDateBetween(start, end).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    // DELETE activity
    @Transactional
    public void deleteActivity(UUID id) {
        if (!activityRepository.existsById(id)) {
            throw new IllegalArgumentException("Activity not found");
        }
        activityRepository.deleteById(id);
    }

    // ────────────────────────────────────────────────────────────────
    // BUSINESS LOGIC: AUTO FINANCIAL RECORDS
    // ────────────────────────────────────────────────────────────────
    private void createFinancialRecordsFromActivity(Activity activity, User creator) {
        // Expense from cost
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

        // Income from sale or birth
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

    // ────────────────────────────────────────────────────────────────
    // BUSINESS LOGIC: BIRTH HANDLING
    // ────────────────────────────────────────────────────────────────
    private void handleBirthActivity(Activity activity) {
        if (activity.getAnimals().size() != 1) {
            throw new IllegalStateException("Birth activity must involve exactly one mother");
        }

        Animal mother = activity.getAnimals().iterator().next();
        if (mother.getGender() != Gender.FEMALE) {
            throw new IllegalStateException("Only female animals can give birth");
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

    // ────────────────────────────────────────────────────────────────
    // MAPPING: Entity → DTO
    // ────────────────────────────────────────────────────────────────
    private ActivityDto mapToDto(Activity activity) {
        ActivityDto dto = new ActivityDto();
        dto.setId(activity.getId());
        dto.setType(activity.getType());
        dto.setDescription(activity.getDescription());
        dto.setDate(activity.getDate());
        dto.setAmount(activity.getAmount());
        dto.setCost(activity.getCost());
        dto.setNotes(activity.getNotes());

        if (activity.getCreatedBy() != null) {
            dto.setCreatedById(activity.getCreatedBy().getId());
            dto.setCreatedByName(
                    activity.getCreatedBy().getFirstName() + " " +
                            activity.getCreatedBy().getLastName()
            );
        }

        dto.setCreatedAt(activity.getCreatedAt());

        dto.setAnimalIds(
                activity.getAnimals().stream()
                        .map(Animal::getId)
                        .collect(Collectors.toList())
        );

        return dto;
    }
}