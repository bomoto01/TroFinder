package com.trofinder.entity;

// ======================
// Import Java
// ======================
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "room_videos")

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomVideo {

    /*
     * ==========================
     * Khóa chính
     * ==========================
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /*
     * ==========================
     * Phòng
     * ==========================
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    /*
     * ==========================
     * Link video
     * ==========================
     */
    @Column(name = "video_url", nullable = false, length = 255)
    private String videoUrl;

    /*
     * ==========================
     * Thời gian upload
     * ==========================
     */
    @CreationTimestamp
    @Column(name = "uploaded_at", updatable = false)
    private LocalDateTime uploadedAt;

}