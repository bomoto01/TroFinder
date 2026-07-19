package com.trofinder.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import com.trofinder.dto.request.RegisterRequest;
import com.trofinder.dto.response.UserResponse;
import com.trofinder.entity.User;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface UserMapper {

    /**
     * RegisterRequest -> User
     */
    @Mapping(target = "id", ignore = true)

    // Password sẽ được BCrypt mã hóa trong Service
    @Mapping(target = "password", ignore = true)

    // Hệ thống tự gán
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "provider", ignore = true)

    // Thông tin mặc định
    @Mapping(target = "avatar", ignore = true)
    @Mapping(target = "zaloPhone", ignore = true)

    // Xác thực
    @Mapping(target = "emailVerified", ignore = true)
    @Mapping(target = "phoneVerified", ignore = true)

    // Uy tín
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "reputationScore", ignore = true)
    @Mapping(target = "averageRating", ignore = true)
    @Mapping(target = "totalReviews", ignore = true)
    @Mapping(target = "responseRate", ignore = true)
    @Mapping(target = "noShowCount", ignore = true)

    // Thời gian
    @Mapping(target = "lastActiveAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)

    User toEntity(RegisterRequest request);

    /**
     * User -> UserResponse
     */
    @Mapping(target = "role", source = "role.name")
    UserResponse toResponse(User user);

}