package com.trofinder.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.trofinder.entity.RoomView;

public interface RoomViewRepository extends JpaRepository<RoomView, Long> {

    Optional<RoomView> findTopByRoomIdAndIpAddressOrderByViewedAtDesc(Long roomId, String ipAddress);

    Optional<RoomView> findTopByRoomIdAndUserIdOrderByViewedAtDesc(Long roomId, Long userId);

    long countByRoomId(Long roomId);

    long countByRoomIdAndViewedAtAfter(Long roomId, LocalDateTime after);
}
