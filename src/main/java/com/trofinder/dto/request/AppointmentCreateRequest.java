package com.trofinder.dto.request;

import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * DTO tạo lịch hẹn xem phòng.
 */
@Data
public class AppointmentCreateRequest {

    /*
     * ==========================================================
     * PHÒNG
     * ==========================================================
     */

    @NotNull(message = "Phòng không được để trống")
    private Long roomId;

    /*
     * ==========================================================
     * THỜI GIAN HẸN
     * ==========================================================
     */

    @NotNull(message = "Ngày hẹn không được để trống")
    @FutureOrPresent(message = "Ngày hẹn phải từ hôm nay trở đi")
    private LocalDate appointmentDate;

    @NotNull(message = "Giờ hẹn không được để trống")
    private LocalTime appointmentTime;

    /*
     * ==========================================================
     * GHI CHÚ
     * ==========================================================
     */

    @Size(max = 1000, message = "Ghi chú không được vượt quá 1000 ký tự")
    private String note;

}