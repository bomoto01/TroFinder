package com.trofinder.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.trofinder.dto.response.ApiResponse;
import com.trofinder.dto.response.NotificationResponse;
import com.trofinder.service.interfaces.NotificationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Validated
public class NotificationController {

    private final NotificationService notificationService;

    /*
     * ==========================================================
     * READ
     * ==========================================================
     */

    /**
     * Tất cả thông báo.
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> getMyNotifications() {

        List<NotificationResponse> response =
                notificationService.getMyNotifications();

        return ResponseEntity.ok(
                ApiResponse.<List<NotificationResponse>>builder()
                        .success(true)
                        .message("Lấy danh sách thông báo thành công.")
                        .data(response)
                        .build());
    }

    /**
     * Thông báo chưa đọc.
     */
    @GetMapping("/unread")
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> getUnreadNotifications() {

        List<NotificationResponse> response =
                notificationService.getUnreadNotifications();

        return ResponseEntity.ok(
                ApiResponse.<List<NotificationResponse>>builder()
                        .success(true)
                        .message("Lấy danh sách thông báo chưa đọc thành công.")
                        .data(response)
                        .build());
    }

    /**
     * Đếm thông báo chưa đọc.
     */
    @GetMapping("/unread/count")
    public ResponseEntity<ApiResponse<Long>> countUnreadNotifications() {

        long count =
                notificationService.countUnreadNotifications();

        return ResponseEntity.ok(
                ApiResponse.<Long>builder()
                        .success(true)
                        .message("Đếm thông báo chưa đọc thành công.")
                        .data(count)
                        .build());
    }

    /*
     * ==========================================================
     * UPDATE
     * ==========================================================
     */

    /**
     * Đánh dấu đã đọc.
     */
    @PutMapping("/{id}/read")
    public ResponseEntity<ApiResponse<NotificationResponse>> markAsRead(
            @PathVariable Long id) {

        NotificationResponse response =
                notificationService.markAsRead(id);

        return ResponseEntity.ok(
                ApiResponse.<NotificationResponse>builder()
                        .success(true)
                        .message("Đánh dấu đã đọc thành công.")
                        .data(response)
                        .build());
    }

    /**
     * Đánh dấu tất cả đã đọc.
     */
    @PutMapping("/read-all")
    public ResponseEntity<ApiResponse<Void>> markAllAsRead() {

        notificationService.markAllAsRead();

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Đã đánh dấu tất cả là đã đọc.")
                        .build());
    }

    /*
     * ==========================================================
     * DELETE
     * ==========================================================
     */

    /**
     * Xóa mềm thông báo.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Long id) {

        notificationService.delete(id);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Xóa thông báo thành công.")
                        .build());
    }

}