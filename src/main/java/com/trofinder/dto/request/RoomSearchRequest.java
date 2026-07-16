package com.trofinder.dto.request;

import java.math.BigDecimal;

import com.trofinder.enums.RoomType;

import lombok.Data;

@Data
public class RoomSearchRequest {

    /**
     * Địa chỉ
     */
    private Long provinceId;

    private Long districtId;

    private Long wardId;

    /**
     * Giá
     */
    private BigDecimal minPrice;

    private BigDecimal maxPrice;

    /**
     * Diện tích
     */
    private BigDecimal minArea;

    private BigDecimal maxArea;

    /**
     * Loại phòng
     */
    private RoomType roomType;

    /**
     * Từ khóa
     */
    private String keyword;

}