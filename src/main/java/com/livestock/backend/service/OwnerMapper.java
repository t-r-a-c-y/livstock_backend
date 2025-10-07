package com.livestock.backend.service;


import com.livestock.backend.dto.request.OwnerRequest;
import com.livestock.backend.dto.response.OwnerResponse;
import com.livestock.backend.model.Owner;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OwnerMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "animals", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Owner toEntity(OwnerRequest request);

    OwnerResponse toResponse(Owner owner);
}