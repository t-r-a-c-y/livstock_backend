package com.livestock.backend.service;

import com.livestock.backend.dto.request.AnimalRequest;
import com.livestock.backend.dto.response.AnimalResponse;
import com.livestock.backend.model.Animal;
import com.livestock.backend.model.Owner;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AnimalMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "activities", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Animal toEntity(AnimalRequest request);

    AnimalResponse toResponse(Animal animal);

    default AnimalResponse.OwnerSummary mapOwnerSummary(Owner owner) {
        if (owner == null) return null;
        AnimalResponse.OwnerSummary summary = new AnimalResponse.OwnerSummary();
        summary.setId(owner.getId());
        summary.setName(owner.getName());
        summary.setEmail(owner.getEmail());
        summary.setPhone(owner.getPhone());
        return summary;
    }
}