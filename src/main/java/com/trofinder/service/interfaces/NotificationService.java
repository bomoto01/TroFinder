package com.trofinder.service.interfaces;

import java.util.List;

import com.trofinder.dto.request.NotificationCreateRequest;
import com.trofinder.dto.response.NotificationResponse;

public interface NotificationService {

    /*
     * ==========================================================
     * CREATE
     * ==========================================================
     */

    NotificationResponse create(NotificationCreateRequest request);

    /*
     * ==========================================================
     * READ
     * ==========================================================
     */

    /**
     * Danh sách tất cả thông báo của người dùng hiện tại.
     */
    List<NotificationResponse> getMyNotifications();

    /**
     * Danh sách thông báo chưa đọc.
     */
    List<NotificationResponse> getUnreadNotifications();

    /**
     * Số lượng thông báo chưa đọc.
     */
    long countUnreadNotifications();

    /*
     * ==========================================================
     * UPDATE
     * ==========================================================
     */

    /**
     * Đánh dấu đã đọc.
     */
    NotificationResponse markAsRead(Long id);

    /**
     * Đánh dấu đọc tất cả.
     */
    void markAllAsRead();

    /*
     * ==========================================================
     * DELETE
     * ==========================================================
     */

    /**
     * Xóa mềm thông báo.
     */
    void delete(Long id);

}