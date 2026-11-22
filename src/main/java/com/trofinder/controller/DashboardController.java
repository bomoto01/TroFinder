package com.trofinder.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.trofinder.dto.response.AdminDashboardResponse;
import com.trofinder.dto.response.ApiResponse;
import com.trofinder.dto.response.OwnerDashboardResponse;
import com.trofinder.service.interfaces.DashboardService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    /**
     * Dành cho chủ trọ / quản lý tin đăng
     */
    @GetMapping("/owner")
    public ResponseEntity<ApiResponse<OwnerDashboardResponse>> getOwnerDashboard() {
        OwnerDashboardResponse response = dashboardService.getOwnerDashboard();
        return ResponseEntity.ok(
                ApiResponse.<OwnerDashboardResponse>builder()
                        .success(true)
                        .message("Lấy dữ liệu bảng điều khiển chủ trọ thành công")
                        .data(response)
                        .build());
    }

    /**
     * Dành cho quản trị viên hệ thống
     */
    @GetMapping("/admin")
    public ResponseEntity<ApiResponse<AdminDashboardResponse>> getAdminDashboard() {
        AdminDashboardResponse response = dashboardService.getAdminDashboard();
        return ResponseEntity.ok(
                ApiResponse.<AdminDashboardResponse>builder()
                        .success(true)
                        .message("Lấy dữ liệu bảng điều khiển quản trị thành công")
                        .data(response)
                        .build());
    }
}
