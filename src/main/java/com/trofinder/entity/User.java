package com.trofinder.entity; // Package chứa Entity User

// ======================
// Import Enum
// ======================
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

/*
 * @Entity
 * Đánh dấu đây là Entity.
 * Hibernate sẽ ánh xạ class này tới bảng users.
 */
@Entity

/*
 * Ánh xạ tới bảng users.
 */
@Table(name = "users")

/*
 * Lombok.
 */
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

    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    @Column(unique = true, length = 100)
    private String email;

    @Column(unique = true, length = 20)
    private String phone;

    /*
     * BCrypt Password
     */
    @Column(nullable = false, length = 255)
    private String password;

    /*
     * Avatar.
     */
    @Column(length = 255)
    private String avatar;

    /*
     * ==========================
     * THÔNG TIN CÁ NHÂN
     * ==========================
     */

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    /*
     * Đăng nhập bằng:
     * LOCAL
     * GOOGLE
     * FACEBOOK
     */
    @Enumerated(EnumType.STRING)
    private Provider provider;

    /*
     * Đã xác minh email chưa.
     */
    @Column(name = "email_verified")
    @Builder.Default
    private Boolean emailVerified = false;

    /*
     * Đã xác minh số điện thoại chưa.
     */
    @Column(name = "phone_verified")
    @Builder.Default
    private Boolean phoneVerified = false;

    /*
     * Trạng thái tài khoản.
     */
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private UserStatus status = UserStatus.PENDING;
    
    /*
    * Điểm uy tín.
    * Mặc định 100.
    */
    @Builder.Default
    @Column(name = "reputation_score")
    private Integer reputationScore = 100;
    /*
    * Số lần đặt lịch nhưng không đến.
    */
    @Builder.Default
    @Column(name = "no_show_count")
    private Integer noShowCount = 0;
    /*
     * ==========================
     * ROLE
     * ==========================
     *
     * Nhiều User thuộc một Role.
     */
    @ManyToOne(fetch = FetchType.LAZY)

    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    /*
     * ==========================
     * THỜI GIAN
     * ==========================
     */

    /*
     * Hibernate tự tạo thời gian khi INSERT.
     */
    @CreationTimestamp
    /*
    * Hoạt động gần nhất.
    */
    @Column(name = "last_active_at")
    private LocalDateTime lastActiveAt;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    /*
     * Hibernate tự cập nhật khi UPDATE.
     */
    @UpdateTimestamp

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

}