package com.livestock.backend.service;

import com.livestock.backend.dto.request.ActivityRequest;
import com.livestock.backend.dto.response.ActivityResponse;
import com.livestock.backend.model.Activity;
import com.livestock.backend.model.Animal;
import com.livestock.backend.model.UserProfile;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ActivityMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "animals", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Activity toEntity(ActivityRequest request);

    @Mapping(target = "createdBy", expression = "java(activity.getCreatedBy() != null ? activity.getCreatedBy().getName() : null)")
    ActivityResponse toResponse(Activity activity);

    default ActivityResponse.AnimalSummary mapAnimalSummary(Animal animal) {
        if (animal == null) return null;
        ActivityResponse.AnimalSummary summary = new ActivityResponse.AnimalSummary();
        summary.setId(animal.getId());
        summary.setTagId(animal.getTagId());
        return summary;
    }

    default List<ActivityResponse.AnimalSummary> mapAnimalSummaries(List<Animal> animals) {
        if (animals == null) return null;
        return animals.stream()
                .map(this::mapAnimalSummary)
                .toList();
    }
}