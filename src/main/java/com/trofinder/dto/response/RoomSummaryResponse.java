package com.trofinder.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.trofinder.enums.ApprovalStatus;
import com.trofinder.enums.RoomStatus;
import com.trofinder.enums.RoomType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomSummaryResponse {

    private Long id;
    private String title;
    private BigDecimal price;
    private BigDecimal deposit;
    private BigDecimal area;
    private String address;

    private Long provinceId;
    private String provinceName;
    private Long districtId;
    private String districtName;
    private Long wardId;
    private String wardName;

    private BigDecimal latitude;
    private BigDecimal longitude;
    private String thumbnail;
    private RoomType roomType;

    private Boolean verified;
    private Boolean featured;

    private Long viewCount;
    private Integer favoriteCount;
    private Double averageRating;
    private Integer reviewCount;

    private RoomStatus status;
    private ApprovalStatus approvalStatus;

    private Long appointmentCount;
    private Long pendingAppointmentCount;

    private Long ownerId;
    private String ownerName;
    private String ownerAvatar;

    private LocalDateTime createdAt;
    private LocalDateTime publishedAt;
}
