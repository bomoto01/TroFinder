package com.trofinder.dto.request;

import java.math.BigDecimal;
import java.util.Set;

import com.trofinder.enums.RoomType;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RoomCreateRequest {

    @NotBlank(message = "Tiêu đề không được để trống")
    @Size(max = 255)
    private String title;

    private String description;

    @NotNull(message = "Giá thuê không được để trống")
    @DecimalMin(value = "0.0")
    private BigDecimal price;

    @NotNull(message = "Diện tích không được để trống")
    @DecimalMin(value = "1.0")
    private BigDecimal area;

    @NotNull(message = "Loại phòng không được để trống")
    private RoomType roomType;

    private BigDecimal deposit;

    private BigDecimal electricPrice;

    private BigDecimal waterPrice;

    @NotBlank(message = "Địa chỉ không được để trống")
    private String address;

    @NotNull(message = "Ward không được để trống")
    private Long wardId;

    private BigDecimal latitude;

    private BigDecimal longitude;

    /**
     * Danh sách tiện ích
     */
    private Set<Long> amenityIds;
    private String additionalAmenities;
}