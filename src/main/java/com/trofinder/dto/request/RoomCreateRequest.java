package com.trofinder.dto.request;

import java.math.BigDecimal;
import java.util.Set;

import com.trofinder.enums.RoomType;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RoomCreateRequest {

    /*
     * ==========================================================
     * THÔNG TIN LIÊN HỆ
     * ==========================================================
     */

    @NotBlank(message = "Tên người liên hệ không được để trống")
    @Size(max = 100)
    private String contactName;

    @NotBlank(message = "Số điện thoại liên hệ không được để trống")
    @Size(max = 20)
    private String contactPhone;

    @Size(max = 20)
    private String zaloPhone;
    @Size(max = 255)
    private String zaloQrUrl;
    /*
     * ==========================================================
     * THÔNG TIN BÀI ĐĂNG
     * ==========================================================
     */

    @NotBlank(message = "Tiêu đề không được để trống")
    @Size(max = 255)
    private String title;

    private String description;

    /*
     * ==========================================================
     * GIÁ
     * ==========================================================
     */

    @NotNull(message = "Giá thuê không được để trống")
    @DecimalMin(value = "0.0", message = "Giá thuê phải lớn hơn hoặc bằng 0")
    private BigDecimal price= BigDecimal.ZERO;;

    @DecimalMin(value = "0.0", message = "Tiền cọc phải lớn hơn hoặc bằng 0")
    private BigDecimal deposit= BigDecimal.ZERO;;

    @DecimalMin(value = "0.0", message = "Giá điện phải lớn hơn hoặc bằng 0")
    private BigDecimal electricPrice= BigDecimal.ZERO;;

    @DecimalMin(value = "0.0", message = "Giá nước phải lớn hơn hoặc bằng 0")
    private BigDecimal waterPrice= BigDecimal.ZERO;;

    /*
     * ==========================================================
     * THÔNG TIN PHÒNG
     * ==========================================================
     */

    @NotNull(message = "Diện tích không được để trống")
    @DecimalMin(value = "1.0", message = "Diện tích phải lớn hơn 0")
    private BigDecimal area;

    @NotNull(message = "Loại phòng không được để trống")
    private RoomType roomType;

    @NotNull(message = "Số lượng phòng không được để trống")
    @Min(value = 1, message = "Số lượng phòng phải lớn hơn 0")
    private Integer quantity;

    /*
     * ==========================================================
     * ĐỊA CHỈ
     * ==========================================================
     */

    @NotBlank(message = "Địa chỉ không được để trống")
    @Size(max = 255)
    private String address;

    @NotNull(message = "Phường/Xã không được để trống")
    private Long wardId;

    private BigDecimal latitude;

    private BigDecimal longitude;

    /*
     * ==========================================================
     * TIỆN ÍCH
     * ==========================================================
     */

    private Set<Long> amenityIds;

    private String additionalAmenities;

    /*
     * ==========================================================
     * CÀI ĐẶT LIÊN HỆ
     * ==========================================================
     */

    private Boolean allowBooking = true;

    private Boolean allowCall = true;

    private Boolean allowChat = true;

}