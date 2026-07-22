package com.trofinder.entity;

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
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Room {

    // ==========================================================
    // PRIMARY KEY
    // ==========================================================

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ==========================================================
    // OWNER
    // ==========================================================

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    // ==========================================================
    // CONTACT
    // ==========================================================

    @Column(name = "contact_name", length = 100)
    private String contactName;

    @Column(name = "contact_phone", length = 20)
    private String contactPhone;

    @Column(name = "zalo_phone", length = 20)
    private String zaloPhone;

    @Column(name = "zalo_qr_url")
    private String zaloQrUrl;

    // ==========================================================
    // BASIC INFORMATION
    // ==========================================================

    @Column(nullable = false, length = 255)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "room_type", nullable = false)
    private RoomType roomType;

    @Column(nullable = false, length = 255)
    private String address;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ward_id", nullable = false)
    private Ward ward;

    @Column(precision = 10, scale = 8)
    private BigDecimal latitude;

    @Column(precision = 11, scale = 8)
    private BigDecimal longitude;

    // ==========================================================
    // PRICE
    // ==========================================================

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    @Builder.Default
    @Column(precision = 12, scale = 2)
    private BigDecimal deposit = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "electric_price", precision = 10, scale = 2)
    private BigDecimal electricPrice = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "water_price", precision = 10, scale = 2)
    private BigDecimal waterPrice = BigDecimal.ZERO;

    @Column(nullable = false, precision = 6, scale = 2)
    private BigDecimal area;

    @Column(columnDefinition = "TEXT")
    private String additionalAmenities;

    // ==========================================================
    // ROOM QUANTITY
    // ==========================================================

    @Builder.Default
    @Column(nullable = false)
    private Integer quantity = 1;

    @Builder.Default
    @Column(name = "available_quantity", nullable = false)
    private Integer availableQuantity = 1;

    // ==========================================================
    // CONTACT SETTINGS
    // ==========================================================

    @Builder.Default
    @Column(name = "allow_booking", nullable = false)
    private Boolean allowBooking = true;

    @Builder.Default
    @Column(name = "allow_call", nullable = false)
    private Boolean allowCall = true;

    @Builder.Default
    @Column(name = "allow_chat", nullable = false)
    private Boolean allowChat = true;

    // ==========================================================
    // STATISTICS
    // ==========================================================

    @Builder.Default
    @Column(name = "view_count", nullable = false)
    private Long viewCount = 0L;

    @Builder.Default
    @Column(name = "favorite_count", nullable = false)
    private Integer favoriteCount = 0;

    @Builder.Default
    @Column(name = "contact_count", nullable = false)
    private Integer contactCount = 0;

    @Builder.Default
    @Column(name = "appointment_count", nullable = false)
    private Integer appointmentCount = 0;

    @Builder.Default
    @Column(name = "share_count", nullable = false)
    private Integer shareCount = 0;

    // ==========================================================
    // DISPLAY
    // ==========================================================

    @Builder.Default
    @Column(nullable = false)
    private Boolean featured = false;

    @Builder.Default
    @Column(name = "priority_score", nullable = false)
    private Integer priorityScore = 0;

    @Builder.Default
    @Column(nullable = false)
    private Boolean verified = false;

    // ==========================================================
    // STATUS
    // ==========================================================

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoomStatus status = RoomStatus.AVAILABLE;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "approval_status", nullable = false)
    private ApprovalStatus approvalStatus = ApprovalStatus.PENDING;

    @Builder.Default
    @Column(nullable = false)
    private Boolean deleted = false;

    // ==========================================================
    // TIME
    // ==========================================================

    @Column(name = "published_at")
    private LocalDateTime publishedAt;

    @Column(name = "boosted_at")
    private LocalDateTime boostedAt;

    @Column(name = "last_contact_at")
    private LocalDateTime lastContactAt;

    @Column(name = "expired_at")
    private LocalDateTime expiredAt;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
        // ==========================================================
    // IMAGES
    // ==========================================================

    @JsonIgnore
    @Builder.Default
    @OneToMany(
            mappedBy = "room",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<RoomImage> images = new ArrayList<>();

    // ==========================================================
    // VIDEOS
    // ==========================================================

    @JsonIgnore
    @Builder.Default
    @OneToMany(
            mappedBy = "room",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<RoomVideo> videos = new ArrayList<>();

    // ==========================================================
    // AMENITIES
    // ==========================================================

    @Builder.Default
    @ManyToMany
    @JoinTable(
            name = "room_amenities",
            joinColumns = @JoinColumn(name = "room_id"),
            inverseJoinColumns = @JoinColumn(name = "amenity_id")
    )
    private Set<Amenity> amenities = new HashSet<>();

}