package com.trofinder.entity;

// ======================
// Import JPA
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
 * Entity ánh xạ tới bảng provinces.
 */
@Entity
@Table(name = "provinces")

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Province {

    /*
     * Khóa chính.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /*
     * Tên tỉnh/thành.
     */
    @Column(nullable = false, unique = true, length = 100)
    private String name;

    /*
     * Mã tỉnh.
     */
    @Column(unique = true, length = 20)
    private String code;

    /*
     * Thời gian tạo.
     */
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

}