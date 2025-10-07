package com.livestock.backend.service;


import com.livestock.backend.dto.request.AnimalRequest;
import com.livestock.backend.dto.response.AnimalResponse;
import com.livestock.backend.model.Animal;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AnimalMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "parent", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Animal toEntity(AnimalRequest request);

    @Mapping(target = "owner", source = "owner")
    @Mapping(target = "parent", source = "parent")
    AnimalResponse toResponse(Animal animal);

    // Add custom mappings if needed for summaries
    default AnimalResponse.OwnerSummary map(Animal.owner owner) {
        if (owner == null) return null;
        AnimalResponse.OwnerSummary summary = new AnimalResponse.OwnerSummary();
        summary.setId(owner.getId());
        summary.setName(owner.getName());
        summary.setEmail(owner.getEmail());
        summary.setPhone(owner.getPhone());
        return summary;
    }

    default AnimalResponse.AnimalSummary map(Animal parent) {
        if (parent == null) return null;
        AnimalResponse.AnimalSummary summary = new AnimalResponse.AnimalSummary();
        summary.setId(parent.getId());
        summary.setTagId(parent.getTagId());
        return summary;
    }
    // Similar for activities
}