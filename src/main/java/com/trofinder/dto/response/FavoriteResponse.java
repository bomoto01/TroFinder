package com.trofinder.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.trofinder.enums.RoomType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO trả về phòng yêu thích.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FavoriteResponse {

    /*
     * ==========================================================
     * FAVORITE
     * ==========================================================
     */
    private LocalDateTime favoriteAt;

    /*
     * ==========================================================
     * ROOM
     * ==========================================================
     */
    private Long roomId;

    private String title;

    private BigDecimal price;

    private BigDecimal area;

    private RoomType roomType;

    private String address;

    /*
     * ==========================================================
     * THUMBNAIL
     * ==========================================================
     */
    private String thumbnail;

    /*
     * ==========================================================
     * OWNER
     * ==========================================================
     */
    private Long ownerId;

    private String ownerName;

    /*
     * ==========================================================
     * STATUS
     * ==========================================================
     */
    private Boolean featured;

    private Boolean verified;

    private Long viewCount;

    /*
     * ==========================================================
     * ROOM CREATED
     * ==========================================================
     */
    private LocalDateTime createdAt;

}