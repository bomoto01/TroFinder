package com.trofinder.entity;

// ======================
// Import Java
// ======================
import java.math.BigDecimal;
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

/*
 * Entity ánh xạ tới bảng universities.
 */
@Entity
@Table(name = "universities")

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class University {

    /*
     * ==========================
     * Khóa chính
     * ==========================
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /*
     * Tên trường
     */
    @Column(nullable = false, length = 255)
    private String name;

    /*
     * Tên viết tắt
     */
    @Column(name = "short_name", length = 100)
    private String shortName;

    /*
     * ==========================
     * Quan hệ với Province
     * ==========================
     */
    @ManyToOne(fetch = FetchType.LAZY)

    @JoinColumn(name = "province_id", nullable = false)
    private Province province;

    /*
     * ==========================
     * Quan hệ với District
     * ==========================
     */
    @ManyToOne(fetch = FetchType.LAZY)

    @JoinColumn(name = "district_id", nullable = false)
    private District district;

    /*
     * ==========================
     * Quan hệ với Ward
     * Có thể NULL
     * ==========================
     */
    @ManyToOne(fetch = FetchType.LAZY)

    @JoinColumn(name = "ward_id")
    private Ward ward;

    /*
     * Địa chỉ đầy đủ
     */
    @Column(length = 255)
    private String address;

    /*
     * Vĩ độ
     */
    @Column(precision = 10, scale = 8)
    private BigDecimal latitude;

    /*
     * Kinh độ
     */
    @Column(precision = 11, scale = 8)
    private BigDecimal longitude;

    /*
     * Thời gian tạo
     */
    @CreationTimestamp

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

}