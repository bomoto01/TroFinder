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

/*
 * Entity ánh xạ tới bảng wards.
 */
@Entity
@Table(name = "wards")

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Ward {

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
     * Quan hệ với District
     * Một quận có nhiều phường.
     * Một phường chỉ thuộc một quận.
     * ==========================
     */
    @ManyToOne(fetch = FetchType.LAZY)

    @JoinColumn(name = "district_id", nullable = false)
    private District district;

    /*
     * ==========================
     * Tên phường/xã
     * ==========================
     */
    @Column(nullable = false, length = 100)
    private String name;

    /*
     * ==========================
     * Mã phường
     * ==========================
     */
    @Column(length = 20)
    private String code;

    /*
     * ==========================
     * Thời gian tạo
     * Hibernate tự sinh
     * ==========================
     */
    @CreationTimestamp

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

}