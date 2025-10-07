package com.livestock.backend.service;


import com.livestock.backend.dto.request.NotificationRequest;
import com.livestock.backend.dto.response.NotificationResponse;
import com.livestock.backend.model.Notification;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface NotificationMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "isRead", ignore = true)
    @Mapping(target = "relatedEntityId", expression = "java(request.getRelatedEntityId() != null ? java.util.UUID.fromString(request.getRelatedEntityId()) : null)")
    Notification toEntity(NotificationRequest request);

    NotificationResponse toResponse(Notification notification);
}
