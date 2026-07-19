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

    /*
     * ==========================
     * Thông tin cơ bản
     * ==========================
     */
    private Long id;

    private String username;

    private String fullName;

    private String email;

    private String phone;

    private String zaloPhone;

    private String avatar;

    /*
     * ==========================
     * Vai trò
     * ==========================
     */
    private String role;

    /*
     * ==========================
     * Trạng thái
     * ==========================
     */
    private UserStatus status;

    private boolean emailVerified;

    private boolean phoneVerified;

    /*
     * ==========================
     * Uy tín
     * ==========================
     */
    private Integer reputationScore;

    private Double averageRating;

    private Integer totalReviews;

    private Double responseRate;

}