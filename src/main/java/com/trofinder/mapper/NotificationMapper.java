package com.trofinder.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import com.trofinder.dto.request.NotificationCreateRequest;
import com.trofinder.dto.response.NotificationResponse;
import com.trofinder.entity.Notification;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface NotificationMapper {

    /*
     * ==========================================================
     * REQUEST -> ENTITY
     * ==========================================================
     */

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "isRead", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Notification toEntity(NotificationCreateRequest request);

    /*
     * ==========================================================
     * ENTITY -> RESPONSE
     * ==========================================================
     */

    NotificationResponse toResponse(Notification notification);

}