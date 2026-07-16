package com.trofinder.dto.response;

import com.trofinder.enums.UserStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    private Long id;

    private String username;

    private String fullName;

    private String email;

    private String phone;

    private String avatar;

    private String role;

    private UserStatus status;

    private boolean emailVerified;

    private boolean phoneVerified;

}