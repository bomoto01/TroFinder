package com.trofinder.dto.request;

import com.trofinder.enums.AppointmentStatus;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * DTO cập nhật lịch hẹn xem phòng.
 */
@Data
public class AppointmentUpdateRequest {

    /*
     * ==========================================================
     * TRẠNG THÁI
     * ==========================================================
     */

    @NotNull(message = "Trạng thái không được để trống")
    private AppointmentStatus status;

    /*
     * ==========================================================
     * GHI CHÚ
     * ==========================================================
     */

    @Size(max = 500, message = "Ghi chú không được vượt quá 500 ký tự")
    private String note;

}