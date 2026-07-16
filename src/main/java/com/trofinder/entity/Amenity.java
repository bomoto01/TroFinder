package com.trofinder.entity;

// ======================
// Import Java
// ======================
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/*
 * Entity ánh xạ tới bảng amenities
 */
@Entity
@Table(name = "amenities")

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Amenity {

    /*
     * Khóa chính
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /*
     * Tên tiện ích
     * Ví dụ:
     * Wifi
     * Điều hòa
     * Máy giặt
     */
    @Column(nullable = false, unique = true, length = 100)
    private String name;

    /*
     * Icon
     * Ví dụ:
     * wifi.png
     * fa-wifi
     */
    @Column(length = 255)
    private String icon;

    /*
     * Ngày tạo
     */
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

}