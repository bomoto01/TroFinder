package com.trofinder.entity;

// ======================
// Import Java
// ======================
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.trofinder.enums.ApprovalStatus;
import com.trofinder.enums.RoomStatus;
import com.trofinder.enums.RoomType;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "rooms")

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Room {

    /*
     * ==========================
     * ID
     * ==========================
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /*
     * Chủ trọ
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    /*
     * Tiêu đề
     */
    @Column(nullable = false, length = 255)
    private String title;

    /*
     * Mô tả
     */
    @Column(columnDefinition = "TEXT")
    private String description;

    /*
     * Giá thuê
     */
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    /*
     * Diện tích
     */
    @Column(nullable = false, precision = 6, scale = 2)
    private BigDecimal area;

    /*
     * Loại phòng
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "room_type", nullable = false)
    private RoomType roomType;

    /*
     * Tiền cọc
     */
    @Builder.Default
    @Column(precision = 12, scale = 2)
    private BigDecimal deposit = BigDecimal.ZERO;

    /*
     * Giá điện
     */
    @Builder.Default
    @Column(name = "electric_price", precision = 10, scale = 2)
    private BigDecimal electricPrice = BigDecimal.ZERO;

    /*
     * Giá nước
     */
    @Builder.Default
    @Column(name = "water_price", precision = 10, scale = 2)
    private BigDecimal waterPrice = BigDecimal.ZERO;

    /*
     * Địa chỉ
     */
    @Column(nullable = false, length = 255)
    private String address;

    /*
     * Phường/Xã
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ward_id", nullable = false)
    private Ward ward;

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
     * Trạng thái phòng
     */
    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoomStatus status = RoomStatus.AVAILABLE;

    /*
     * Trạng thái duyệt
     */
    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "approval_status", nullable = false)
    private ApprovalStatus approvalStatus = ApprovalStatus.PENDING;

    /*
     * Lượt xem
     */
    @Builder.Default
    @Column(name = "view_count")
    private Long viewCount = 0L;
    /*
    * Xóa mềm.
    */
     @Builder.Default
     @Column(nullable = false)
     private Boolean deleted = false;

    /*
     * Ngày tạo
     */
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    /*
     * Ngày cập nhật
     */
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /*
     * ==========================
     * Danh sách ảnh
     * ==========================
     */
    @JsonIgnore
    @Builder.Default
    @OneToMany(
            mappedBy = "room",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<RoomImage> images = new ArrayList<>();

    /*
     * ==========================
     * Danh sách video
     * ==========================
     */
    @JsonIgnore
    @Builder.Default
    @OneToMany(
            mappedBy = "room",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<RoomVideo> videos = new ArrayList<>();

    /*
     * ==========================
     * Danh sách tiện ích
     * ==========================
     */
    @Builder.Default
    @ManyToMany
    @JoinTable(
            name = "room_amenities",
            joinColumns = @JoinColumn(name = "room_id"),
            inverseJoinColumns = @JoinColumn(name = "amenity_id")
    )
    private Set<Amenity> amenities = new HashSet<>();
        @Column(columnDefinition = "TEXT")
        private String additionalAmenities;

}