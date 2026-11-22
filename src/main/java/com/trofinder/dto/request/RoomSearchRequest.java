package com.trofinder.dto.request;

import java.math.BigDecimal;
import java.util.List;

import com.trofinder.enums.RoomStatus;
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

    /**
     * Đã xác thực
     */
    private Boolean verified;

    /**
     * Nổi bật
     */
    private Boolean featured;

    /**
     * Trạng thái phòng (mặc định AVAILABLE cho người tìm kiếm)
     */
    private RoomStatus status;

    /**
     * Danh sách mã tiện ích yêu cầu
     */
    private List<Long> amenityIds;

    /**
     * Kiểu sắp xếp (e.g. price_asc, price_desc, area_asc, area_desc, newest, views, favorites)
     */
    private String sortBy;
}
