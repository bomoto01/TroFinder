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
 * Entity ánh xạ tới bảng districts.
 */
@Entity
@Table(name = "districts")

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class District {

    /*
     * Khóa chính.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /*
     * Một tỉnh có nhiều quận.
     * Một quận chỉ thuộc một tỉnh.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "province_id", nullable = false)
    private Province province;

    /*
     * Tên quận/huyện.
     */
    @Column(nullable = false, length = 100)
    private String name;

    /*
     * Mã quận.
     */
    @Column(length = 20)
    private String code;

    /*
     * Thời gian tạo.
     */
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

}