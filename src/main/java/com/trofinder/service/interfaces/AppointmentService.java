package com.trofinder.service.interfaces;

import java.util.List;

import com.trofinder.dto.request.AppointmentCreateRequest;
import com.trofinder.dto.request.AppointmentUpdateRequest;
import com.trofinder.dto.response.AppointmentResponse;

public interface AppointmentService {

    /*
     * ==========================================================
     * CREATE
     * ==========================================================
     */

    /**
     * Người thuê đặt lịch xem phòng.
     */
    AppointmentResponse create(AppointmentCreateRequest request);

    /*
     * ==========================================================
     * READ
     * ==========================================================
     */

    /**
     * Chi tiết lịch hẹn.
     */
    AppointmentResponse getById(Long id);

    /**
     * Danh sách lịch hẹn của người thuê.
     */
    List<AppointmentResponse> getMyAppointments();

    /**
     * Danh sách lịch hẹn của chủ trọ.
     */
    List<AppointmentResponse> getOwnerAppointments();
    /**
     * Danh sách lịch hẹn của một phòng.
     */
    List<AppointmentResponse> getRoomAppointments(Long roomId);
    /*
     * ==========================================================
     * UPDATE
     * ==========================================================
     */

    /**
     * Chủ trọ cập nhật trạng thái lịch hẹn.
     */
    AppointmentResponse updateStatus(
            Long id,
            AppointmentUpdateRequest request);

    /*
     * ==========================================================
     * DELETE
     * ==========================================================
     */

    /**
     * Hủy lịch hẹn (xóa mềm).
     */
    void delete(Long id);

}