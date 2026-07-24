package com.trofinder.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.trofinder.dto.request.NotificationCreateRequest;
import com.trofinder.dto.response.NotificationResponse;
import com.trofinder.entity.Notification;
import com.trofinder.entity.User;
import com.trofinder.exception.ResourceNotFoundException;
import com.trofinder.mapper.NotificationMapper;
import com.trofinder.repository.NotificationRepository;
import com.trofinder.repository.UserRepository;
import com.trofinder.service.interfaces.CurrentUserService;
import com.trofinder.service.interfaces.NotificationService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationServiceImpl implements NotificationService {

    /*
     * ==========================================================
     * DEPENDENCY
     * ==========================================================
     */

    private final NotificationRepository notificationRepository;

    private final UserRepository userRepository;

    private final NotificationMapper notificationMapper;

    private final CurrentUserService currentUserService;

    /*
     * ==========================================================
     * CREATE
     * ==========================================================
     */

    @Override
    @Transactional
    public NotificationResponse create(NotificationCreateRequest request) {

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Không tìm thấy người dùng."));

        Notification notification =
                notificationMapper.toEntity(request);

        notification.setUser(user);

        Notification savedNotification =
                notificationRepository.save(notification);

        return notificationMapper.toResponse(savedNotification);
    }

    /*
     * ==========================================================
     * READ
     * ==========================================================
     */

    @Override
    public List<NotificationResponse> getMyNotifications() {

        User currentUser = getCurrentUser();

        return notificationRepository
                .findByUserAndDeletedFalseOrderByCreatedAtDesc(currentUser)
                .stream()
                .map(notificationMapper::toResponse)
                .toList();
    }

    @Override
    public List<NotificationResponse> getUnreadNotifications() {

        User currentUser = getCurrentUser();

        return notificationRepository
                .findByUserAndIsReadFalseAndDeletedFalseOrderByCreatedAtDesc(currentUser)
                .stream()
                .map(notificationMapper::toResponse)
                .toList();
    }

    @Override
    public long countUnreadNotifications() {

        return notificationRepository
                .countByUserAndIsReadFalseAndDeletedFalse(
                        getCurrentUser());
    }
        /*
     * ==========================================================
     * UPDATE
     * ==========================================================
     */

    @Override
    @Transactional
    public NotificationResponse markAsRead(Long id) {

        Notification notification = getNotification(id);

        validateOwner(notification);

        notification.setIsRead(true);

        Notification savedNotification =
                notificationRepository.save(notification);

        return notificationMapper.toResponse(savedNotification);
    }

    @Override
    @Transactional
    public void markAllAsRead() {

        User currentUser = getCurrentUser();

        List<Notification> notifications =
                notificationRepository
                        .findByUserAndIsReadFalseAndDeletedFalseOrderByCreatedAtDesc(
                                currentUser);

        notifications.forEach(notification ->
                notification.setIsRead(true));

        notificationRepository.saveAll(notifications);
    }

    /*
     * ==========================================================
     * DELETE
     * ==========================================================
     */

    @Override
    @Transactional
    public void delete(Long id) {

        Notification notification = getNotification(id);

        validateOwner(notification);

        notification.setDeleted(true);

        notificationRepository.save(notification);
    }
        /*
     * ==========================================================
     * PRIVATE METHODS
     * ==========================================================
     */

    /**
     * Lấy người dùng hiện tại.
     */
    private User getCurrentUser() {

        return currentUserService.getCurrentUser();
    }

    /**
     * Lấy thông báo theo ID.
     */
    private Notification getNotification(Long id) {

        return notificationRepository
                .findByIdAndDeletedFalse(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Không tìm thấy thông báo."));
    }

    /**
     * Kiểm tra quyền sở hữu thông báo.
     */
    private void validateOwner(Notification notification) {

        User currentUser = getCurrentUser();

        if (!notification.getUser().getId().equals(currentUser.getId())) {

            throw new ResourceNotFoundException(
                    "Bạn không có quyền truy cập thông báo này.");
        }
    }

}