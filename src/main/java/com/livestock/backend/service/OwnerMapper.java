package com.livestock.backend.service;

import com.livestock.backend.dto.request.OwnerRequest;
import com.livestock.backend.dto.response.OwnerResponse;
import com.livestock.backend.model.Animal;
import com.livestock.backend.model.Owner;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OwnerMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "animals", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "financialRecords", ignore = true)
    Owner toEntity(OwnerRequest request);

    @Mapping(target = "animals", source = "animals")
    @Mapping(target = "totalAnimals", expression = "java(owner.getAnimals() != null ? owner.getAnimals().size() : 0)")
    OwnerResponse toResponse(Owner owner);

    default OwnerResponse.AnimalSummary mapAnimalSummary(Animal animal) {
        if (animal == null) return null;
        OwnerResponse.AnimalSummary summary = new OwnerResponse.AnimalSummary();
        summary.setId(animal.getId());
        summary.setTagId(animal.getTagId());
        summary.setType(animal.getType());
        summary.setStatus(animal.getStatus());
        return summary;
    }

    default List<OwnerResponse.AnimalSummary> mapAnimalSummaries(List<Animal> animals) {
        if (animals == null) return null;
        return animals.stream()
                .map(this::mapAnimalSummary)
                .toList();
    }
}