package com.trofinder.service.impl;

import java.time.LocalDateTime;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.trofinder.entity.Room;
import com.trofinder.entity.RoomView;
import com.trofinder.entity.User;
import com.trofinder.repository.RoomRepository;
import com.trofinder.repository.RoomViewRepository;
import com.trofinder.repository.UserRepository;
import com.trofinder.service.interfaces.RoomViewService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RoomViewServiceImpl implements RoomViewService {

    private final RoomViewRepository roomViewRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;

    private static final int COOLDOWN_MINUTES = 10;

    @Override
    @Transactional
    public void recordView(Long roomId, String ipAddress, Long userId) {
        Room room = roomRepository.findByIdAndDeletedFalse(roomId).orElse(null);
        if (room == null) {
            return;
        }

        LocalDateTime cutoff = LocalDateTime.now().minusMinutes(COOLDOWN_MINUTES);

        // Check user cooldown
        if (userId != null) {
            boolean recentlyViewedByUser = roomViewRepository.findTopByRoomIdAndUserIdOrderByViewedAtDesc(roomId, userId)
                    .map(v -> v.getViewedAt() != null && v.getViewedAt().isAfter(cutoff))
                    .orElse(false);
            if (recentlyViewedByUser) {
                return;
            }
        }

        // Check IP cooldown
        if (ipAddress != null && !ipAddress.isBlank()) {
            boolean recentlyViewedByIp = roomViewRepository.findTopByRoomIdAndIpAddressOrderByViewedAtDesc(roomId, ipAddress)
                    .map(v -> v.getViewedAt() != null && v.getViewedAt().isAfter(cutoff))
                    .orElse(false);
            if (recentlyViewedByIp) {
                return;
            }
        }

        User user = userId != null ? userRepository.findById(userId).orElse(null) : null;

        RoomView view = RoomView.builder()
                .room(room)
                .user(user)
                .ipAddress(ipAddress)
                .build();

        roomViewRepository.save(view);

        // Increment counter on room entity
        room.setViewCount(room.getViewCount() + 1);
        roomRepository.save(room);
    }

    @Override
    public long getTotalViews(Long roomId) {
        return roomViewRepository.countByRoomId(roomId);
    }
}
