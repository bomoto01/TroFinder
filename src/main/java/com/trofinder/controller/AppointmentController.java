package com.trofinder.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.trofinder.dto.request.AppointmentCreateRequest;
import com.trofinder.dto.request.AppointmentUpdateRequest;
import com.trofinder.dto.response.ApiResponse;
import com.trofinder.dto.response.AppointmentResponse;
import com.trofinder.service.interfaces.AppointmentService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
@Validated
public class AppointmentController {

    private final AppointmentService appointmentService;

    /*
     * ==========================================================
     * CREATE
     * ==========================================================
     */

    /**
     * Người thuê đặt lịch xem phòng.
     */
    @PostMapping
    public ResponseEntity<ApiResponse<AppointmentResponse>> create(
            @Valid @RequestBody AppointmentCreateRequest request) {

        AppointmentResponse response =
                appointmentService.create(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<AppointmentResponse>builder()
                        .success(true)
                        .message("Đặt lịch thành công.")
                        .data(response)
                        .build());
    }

    /*
     * ==========================================================
     * READ
     * ==========================================================
     */

    /**
     * Chi tiết lịch hẹn.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AppointmentResponse>> getById(
            @PathVariable Long id) {

        AppointmentResponse response =
                appointmentService.getById(id);

        return ResponseEntity.ok(
                ApiResponse.<AppointmentResponse>builder()
                        .success(true)
                        .message("Lấy lịch hẹn thành công.")
                        .data(response)
                        .build());
    }

    /**
     * Danh sách lịch hẹn của người thuê.
     */
    @GetMapping("/my")
    public ResponseEntity<ApiResponse<List<AppointmentResponse>>> getMyAppointments() {

        List<AppointmentResponse> response =
                appointmentService.getMyAppointments();

        return ResponseEntity.ok(
                ApiResponse.<List<AppointmentResponse>>builder()
                        .success(true)
                        .message("Lấy danh sách lịch hẹn thành công.")
                        .data(response)
                        .build());
    }

    /**
     * Danh sách lịch hẹn của chủ trọ.
     */
    @GetMapping("/owner")
    public ResponseEntity<ApiResponse<List<AppointmentResponse>>> getOwnerAppointments() {

        List<AppointmentResponse> response =
                appointmentService.getOwnerAppointments();

        return ResponseEntity.ok(
                ApiResponse.<List<AppointmentResponse>>builder()
                        .success(true)
                        .message("Lấy danh sách lịch hẹn của chủ trọ thành công.")
                        .data(response)
                        .build());
    }

    /*
     * ==========================================================
     * UPDATE
     * ==========================================================
     */

    /**
     * Chủ trọ cập nhật trạng thái lịch hẹn.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<AppointmentResponse>> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody AppointmentUpdateRequest request) {

        AppointmentResponse response =
                appointmentService.updateStatus(id, request);

        return ResponseEntity.ok(
                ApiResponse.<AppointmentResponse>builder()
                        .success(true)
                        .message("Cập nhật lịch hẹn thành công.")
                        .data(response)
                        .build());
    }

    /*
     * ==========================================================
     * DELETE
     * ==========================================================
     */

    /**
     * Hủy lịch hẹn.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Long id) {

        appointmentService.delete(id);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Hủy lịch hẹn thành công.")
                        .build());
    }
    /**
     * Danh sách lịch hẹn của một phòng.
     */
    @GetMapping("/room/{roomId}")
    public ResponseEntity<ApiResponse<List<AppointmentResponse>>> getRoomAppointments(
            @PathVariable Long roomId) {

        List<AppointmentResponse> response =
                appointmentService.getRoomAppointments(roomId);

        return ResponseEntity.ok(
                ApiResponse.<List<AppointmentResponse>>builder()
                        .success(true)
                        .message("Lấy lịch hẹn của phòng thành công.")
                        .data(response)
                        .build());
    }
}