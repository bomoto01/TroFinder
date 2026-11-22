package com.trofinder.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.trofinder.entity.Review;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    Page<Review> findByRoomIdOrderByCreatedAtDesc(Long roomId, Pageable pageable);

    boolean existsByRoomIdAndUserId(Long roomId, Long userId);

    Optional<Review> findByRoomIdAndUserId(Long roomId, Long userId);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.room.id = :roomId")
    Double getAverageRatingByRoomId(@Param("roomId") Long roomId);

    @Query("SELECT COUNT(r) FROM Review r WHERE r.room.id = :roomId")
    Long countByRoomId(@Param("roomId") Long roomId);
}
