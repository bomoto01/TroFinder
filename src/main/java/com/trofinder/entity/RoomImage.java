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
@Table(name = "room_images")

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomImage {

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
     * Link ảnh
     * ==========================
     */
    @Column(name = "image_url", nullable = false)
    private String imageUrl;

    /*
     * ==========================
     * Thứ tự hiển thị
     * ==========================
     */
    @Builder.Default
    @Column(name = "display_order")
    private Integer displayOrder = 1;

    /*
     * ==========================
     * Có phải ảnh đại diện không
     * ==========================
     */
    @Builder.Default
    @Column(name = "is_thumbnail")
    private Boolean thumbnail = false;

    /*
     * ==========================
     * Ngày upload
     * ==========================
     */
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

}