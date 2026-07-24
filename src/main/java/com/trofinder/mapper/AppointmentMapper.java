package com.trofinder.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import com.trofinder.dto.request.AppointmentCreateRequest;
import com.trofinder.dto.response.AppointmentResponse;
import com.trofinder.entity.Appointment;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface AppointmentMapper {

    /*
     * ==========================================================
     * CREATE REQUEST -> ENTITY
     * ==========================================================
     */

    @Mapping(target = "id", ignore = true)

    @Mapping(target = "room", ignore = true)
    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "tenant", ignore = true)

    @Mapping(target = "status", ignore = true)

    @Mapping(target = "confirmedAt", ignore = true)
    @Mapping(target = "cancelledAt", ignore = true)
    @Mapping(target = "completedAt", ignore = true)

    @Mapping(target = "deleted", ignore = true)

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)

    Appointment toEntity(AppointmentCreateRequest request);

    /*
     * ==========================================================
     * ENTITY -> RESPONSE
     * ==========================================================
     */

    // Appointment
    @Mapping(target = "id", source = "id")
    @Mapping(target = "appointmentDate", source = "appointmentDate")
    @Mapping(target = "appointmentTime", source = "appointmentTime")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "note", source = "note")

    // Room
    @Mapping(target = "roomId", source = "room.id")
    @Mapping(target = "roomTitle", source = "room.title")
    @Mapping(target = "roomAddress", source = "room.address")
    @Mapping(target = "roomPrice", source = "room.price")
    @Mapping(target = "roomArea", source = "room.area")

    @Mapping(target = "allowCall", source = "room.allowCall")
    @Mapping(target = "allowChat", source = "room.allowChat")

    // Owner
    @Mapping(target = "ownerId", source = "owner.id")
    @Mapping(target = "ownerName", source = "owner.fullName")
    @Mapping(target = "ownerPhone", source = "owner.phone")

    // Tenant
    @Mapping(target = "tenantId", source = "tenant.id")
    @Mapping(target = "tenantName", source = "tenant.fullName")
    @Mapping(target = "tenantPhone", source = "tenant.phone")

    // Process time
    @Mapping(target = "confirmedAt", source = "confirmedAt")
    @Mapping(target = "cancelledAt", source = "cancelledAt")
    @Mapping(target = "completedAt", source = "completedAt")

    // Audit
    @Mapping(target = "deleted", source = "deleted")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "updatedAt", source = "updatedAt")

    AppointmentResponse toResponse(Appointment appointment);

}