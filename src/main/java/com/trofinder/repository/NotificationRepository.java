package com.trofinder.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.trofinder.entity.Notification;
import com.trofinder.entity.User;
import com.trofinder.enums.NotificationType;

public interface NotificationRepository
        extends JpaRepository<Notification, Long> {

    /*
     * ==========================================================
     * CRUD
     * ==========================================================
     */

    /**
     * Lấy thông báo theo ID (chưa bị xóa).
     */
    Optional<Notification> findByIdAndDeletedFalse(Long id);

    /*
     * ==========================================================
     * USER
     * ==========================================================
     */

    /**
     * Danh sách tất cả thông báo.
     */
    List<Notification> findByUserAndDeletedFalseOrderByCreatedAtDesc(
            User user);

    /**
     * Danh sách thông báo chưa đọc.
     */
    List<Notification> findByUserAndIsReadFalseAndDeletedFalseOrderByCreatedAtDesc(
            User user);

    /**
     * Đếm số thông báo chưa đọc.
     */
    long countByUserAndIsReadFalseAndDeletedFalse(
            User user);

    /*
     * ==========================================================
     * TYPE
     * ==========================================================
     */

    /**
     * Lọc theo loại thông báo.
     */
    List<Notification> findByUserAndTypeAndDeletedFalseOrderByCreatedAtDesc(
            User user,
            NotificationType type);

    /**
     * Lọc theo trạng thái đọc.
     */
    List<Notification> findByUserAndIsReadAndDeletedFalseOrderByCreatedAtDesc(
            User user,
            Boolean isRead);

}