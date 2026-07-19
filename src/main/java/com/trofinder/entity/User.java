package com.trofinder.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.trofinder.enums.Gender;
import com.trofinder.enums.Provider;
import com.trofinder.enums.UserStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "users")

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    /*
     * ==========================
     * PRIMARY KEY
     * ==========================
     */

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /*
     * ==========================
     * THÔNG TIN ĐĂNG NHẬP
     * ==========================
     */

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false, length = 255)
    private String password;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Provider provider = Provider.LOCAL;

    /*
     * ==========================
     * ROLE
     * ==========================
     */

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    /*
     * ==========================
     * THÔNG TIN CÁ NHÂN
     * ==========================
     */

    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    @Column(unique = true, length = 100)
    private String email;

    @Column(unique = true, length = 20)
    private String phone;

    @Column(length = 255)
    private String avatar;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Gender gender = Gender.OTHER;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    /*
     * ==========================
     * XÁC THỰC
     * ==========================
     */

    @Builder.Default
    @Column(name = "email_verified")
    private Boolean emailVerified = false;

    @Builder.Default
    @Column(name = "phone_verified")
    private Boolean phoneVerified = false;

    @Column(name = "zalo_phone", length = 20)
    private String zaloPhone;

    /*
     * ==========================
     * TRẠNG THÁI TÀI KHOẢN
     * ==========================
     */

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private UserStatus status = UserStatus.PENDING;

    @Builder.Default
    @Column(nullable = false)
    private Boolean deleted = false;

    /*
     * ==========================
     * ĐIỂM UY TÍN
     * ==========================
     */

    @Builder.Default
    @Column(name = "reputation_score")
    private Integer reputationScore = 100;

    @Builder.Default
    @Column(name = "average_rating")
    private Double averageRating = 0.0;

    @Builder.Default
    @Column(name = "total_reviews")
    private Integer totalReviews = 0;

    @Builder.Default
    @Column(name = "response_rate")
    private Double responseRate = 100.0;

    @Builder.Default
    @Column(name = "no_show_count")
    private Short noShowCount = 0;

    /*
     * ==========================
     * HOẠT ĐỘNG
     * ==========================
     */

    @Column(name = "last_active_at")
    private LocalDateTime lastActiveAt;

    /*
     * ==========================
     * THỜI GIAN
     * ==========================
     */

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

}