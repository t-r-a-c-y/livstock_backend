package com.livestock.backend.service;

import com.livestock.backend.dto.request.UserRequest;
import com.livestock.backend.dto.response.UserResponse;
import com.livestock.backend.model.UserProfile;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "avatar", ignore = true)
    @Mapping(target = "status", ignore = true)
    UserProfile toEntity(UserRequest request);

    UserResponse toResponse(UserProfile user);
}