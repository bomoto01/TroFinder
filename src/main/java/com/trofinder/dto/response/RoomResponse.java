package com.trofinder.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import com.trofinder.enums.ApprovalStatus;
import com.trofinder.enums.RoomStatus;
import com.trofinder.enums.RoomType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO trả về thông tin phòng.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomResponse {

    /*
     * ==========================
     * Thông tin cơ bản
     * ==========================
     */
    private Long id;

    /*
     * ==========================
     * Chủ phòng
     * ==========================
     */
    private Long ownerId;

    private String ownerName;

    private String ownerPhone;

    /*
     * ==========================
     * Thông tin phòng
     * ==========================
     */
    private String title;

    private String description;

    private BigDecimal price;

    private BigDecimal area;

    private RoomType roomType;

    /*
     * ==========================
     * Chi phí
     * ==========================
     */
    private BigDecimal deposit;

    private BigDecimal electricPrice;

    private BigDecimal waterPrice;

    /*
     * ==========================
     * Địa chỉ
     * ==========================
     */
    private String address;

    private Long provinceId;

    private String provinceName;

    private Long districtId;

    private String districtName;

    private Long wardId;

    private String wardName;

    /*
     * ==========================
     * Tọa độ
     * ==========================
     */
    private BigDecimal latitude;

    private BigDecimal longitude;

    /*
     * ==========================
     * Trạng thái
     * ==========================
     */
    private RoomStatus status;

    private ApprovalStatus approvalStatus;

    /*
     * ==========================
     * Thống kê
     * ==========================
     */
    private Long viewCount;

    /*
     * ==========================
     * Danh sách ảnh
     * ==========================
     */
    private List<String> imageUrls;

    /*
     * ==========================
     * Danh sách video
     * ==========================
     */
    private List<String> videoUrls;

    /*
     * ==========================
     * Tiện ích
     * ==========================
     */
    private Set<String> amenities;

    /**
     * Tiện ích khác do chủ trọ nhập.
     */
    private String additionalAmenities;

    /*
     * ==========================
     * Thời gian
     * ==========================
     */
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}