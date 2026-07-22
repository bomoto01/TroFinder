package com.trofinder.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entity lưu danh sách phòng yêu thích.
 */
@Entity
@Table(name = "favorites")

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Favorite {

    /*
     * ==========================================================
     * KHÓA CHÍNH KÉP
     * ==========================================================
     */
    @EmbeddedId
    private FavoriteId id;

    /*
     * ==========================================================
     * NGƯỜI DÙNG
     * ==========================================================
     */
    @MapsId("userId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /*
     * ==========================================================
     * PHÒNG
     * ==========================================================
     */
    @MapsId("roomId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    /*
     * ==========================================================
     * THỜI GIAN THÊM YÊU THÍCH
     * ==========================================================
     */
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

}