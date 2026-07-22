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
     * ==========================================================
     * THÔNG TIN CƠ BẢN
     * ==========================================================
     */
    private Long id;

    /*
     * ==========================================================
     * CHỦ BÀI ĐĂNG
     * ==========================================================
     */
    private Long ownerId;

    private String ownerName;

    private String ownerPhone;

    private String ownerAvatar;

    /*
     * ==========================================================
     * THÔNG TIN LIÊN HỆ
     * ==========================================================
     */
    private String contactName;

    private String contactPhone;

    private String zaloPhone;

    private String zaloQrUrl;

    /*
     * ==========================================================
     * THÔNG TIN BÀI ĐĂNG
     * ==========================================================
     */
    private String title;

    private String description;

    private RoomType roomType;

    /*
     * ==========================================================
     * GIÁ
     * ==========================================================
     */
    private BigDecimal price;

    private BigDecimal deposit;

    private BigDecimal electricPrice;

    private BigDecimal waterPrice;

    private BigDecimal area;

    /*
     * ==========================================================
     * SỐ LƯỢNG PHÒNG
     * ==========================================================
     */
    private Integer quantity;

    private Integer availableQuantity;

    /*
     * ==========================================================
     * ĐỊA CHỈ
     * ==========================================================
     */
    private String address;

    private Long provinceId;

    private String provinceName;

    private Long districtId;

    private String districtName;

    private Long wardId;

    private String wardName;

    /*
     * ==========================================================
     * TỌA ĐỘ
     * ==========================================================
     */
    private BigDecimal latitude;

    private BigDecimal longitude;

    /*
     * ==========================================================
     * TIỆN ÍCH
     * ==========================================================
     */
    private Set<String> amenities;

    private String additionalAmenities;

    /*
     * ==========================================================
     * CÀI ĐẶT LIÊN HỆ
     * ==========================================================
     */
    private Boolean allowBooking;

    private Boolean allowCall;

    private Boolean allowChat;

    /*
     * ==========================================================
     * THỐNG KÊ
     * ==========================================================
     */
    private Long viewCount;

    private Integer favoriteCount;

    private Integer contactCount;

    private Integer appointmentCount;

    private Integer shareCount;

    /*
     * ==========================================================
     * HIỂN THỊ
     * ==========================================================
     */
    private Boolean featured;

    private Boolean verified;

    private Integer priorityScore;

    /*
     * ==========================================================
     * TRẠNG THÁI
     * ==========================================================
     */
    private RoomStatus status;

    private ApprovalStatus approvalStatus;

    /*
     * ==========================================================
     * HÌNH ẢNH
     * ==========================================================
     */
    private List<String> imageUrls;

    /*
     * ==========================================================
     * VIDEO
     * ==========================================================
     */
    private List<String> videoUrls;

    /*
     * ==========================================================
     * THỜI GIAN
     * ==========================================================
     */
    private LocalDateTime publishedAt;

    private LocalDateTime boostedAt;

    private LocalDateTime lastContactAt;

    private LocalDateTime expiredAt;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}