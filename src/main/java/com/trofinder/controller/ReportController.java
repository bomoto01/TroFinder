package com.trofinder.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.trofinder.dto.request.ReportCreateRequest;
import com.trofinder.dto.response.ApiResponse;
import com.trofinder.dto.response.ReportResponse;
import com.trofinder.service.interfaces.ReportService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    /**
     * Gửi báo cáo phòng vi phạm
     */
    @PostMapping
    public ResponseEntity<ApiResponse<ReportResponse>> createReport(
            @Valid @RequestBody ReportCreateRequest request) {

        ReportResponse response = reportService.createReport(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<ReportResponse>builder()
                        .success(true)
                        .message("Gửi báo cáo vi phạm thành công")
                        .data(response)
                        .build());
    }

    /**
     * Xem danh sách báo cáo do người dùng hiện tại gửi
     */
    @GetMapping("/my")
    public ResponseEntity<ApiResponse<List<ReportResponse>>> getMyReports() {

        List<ReportResponse> response = reportService.getMyReports();

        return ResponseEntity.ok(
                ApiResponse.<List<ReportResponse>>builder()
                        .success(true)
                        .message("Lấy danh sách báo cáo thành công")
                        .data(response)
                        .build());
    }
}
