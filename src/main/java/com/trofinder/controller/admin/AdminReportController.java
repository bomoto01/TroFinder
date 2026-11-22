package com.trofinder.controller.admin;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.trofinder.dto.request.ReportProcessRequest;
import com.trofinder.dto.response.ApiResponse;
import com.trofinder.dto.response.ReportHistoryResponse;
import com.trofinder.dto.response.ReportResponse;
import com.trofinder.enums.ReportStatus;
import com.trofinder.service.interfaces.ReportService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/reports")
@RequiredArgsConstructor
public class AdminReportController {

    private final ReportService reportService;

    /**
     * Danh sách báo cáo vi phạm (Dành cho Quản trị viên)
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Page<ReportResponse>>> getAllReports(
            @RequestParam(required = false) ReportStatus status,
            @PageableDefault(size = 10) Pageable pageable) {

        Page<ReportResponse> response = reportService.getAllReports(status, pageable);

        return ResponseEntity.ok(
                ApiResponse.<Page<ReportResponse>>builder()
                        .success(true)
                        .message("Lấy danh sách báo cáo thành công")
                        .data(response)
                        .build());
    }

    /**
     * Chi tiết báo cáo
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ReportResponse>> getReportById(
            @PathVariable Long id) {

        ReportResponse response = reportService.getReportById(id);

        return ResponseEntity.ok(
                ApiResponse.<ReportResponse>builder()
                        .success(true)
                        .message("Lấy chi tiết báo cáo thành công")
                        .data(response)
                        .build());
    }

    /**
     * Lịch sử xử lý báo cáo
     */
    @GetMapping("/{id}/histories")
    public ResponseEntity<ApiResponse<List<ReportHistoryResponse>>> getReportHistories(
            @PathVariable Long id) {

        List<ReportHistoryResponse> response = reportService.getReportHistories(id);

        return ResponseEntity.ok(
                ApiResponse.<List<ReportHistoryResponse>>builder()
                        .success(true)
                        .message("Lấy lịch sử xử lý báo cáo thành công")
                        .data(response)
                        .build());
    }

    /**
     * Xử lý báo cáo vi phạm
     */
    @PutMapping("/{id}/process")
    public ResponseEntity<ApiResponse<ReportResponse>> processReport(
            @PathVariable Long id,
            @Valid @RequestBody ReportProcessRequest request) {

        ReportResponse response = reportService.processReport(id, request);

        return ResponseEntity.ok(
                ApiResponse.<ReportResponse>builder()
                        .success(true)
                        .message("Xử lý báo cáo thành công")
                        .data(response)
                        .build());
    }
}
