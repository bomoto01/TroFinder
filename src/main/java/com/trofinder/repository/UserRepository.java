package com.trofinder.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.trofinder.entity.User;
import com.trofinder.enums.UserStatus;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /* ==========================================================
                            LOGIN
       ========================================================== */

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    Optional<User> findByPhone(String phone);

    /**
     * Đăng nhập bằng username hoặc email.
     * Sau này chỉ cần sửa query là có thể hỗ trợ thêm phone.
     */
    @Query("""
            SELECT u
            FROM User u
            WHERE u.username = :login
               OR u.email = :login
               OR u.phone = :login
            """)
    Optional<User> findByLogin(@Param("login") String login);

    /**
 * Đăng nhập và lấy luôn Role (tránh LazyInitializationException).
   */
   @Query("""
         SELECT u
         FROM User u
         JOIN FETCH u.role
         WHERE u.username = :login
            OR u.email = :login
            OR u.phone = :login
         """)
   Optional<User> findByLoginWithRole(@Param("login") String login);
    /* ==========================================================
                        KIỂM TRA TỒN TẠI
       ========================================================== */

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByPhone(String phone);

    /* ==========================================================
                        QUẢN LÝ USER
       ========================================================== */

    List<User> findByStatus(UserStatus status);

    List<User> findByRole_Name(String roleName);

    List<User> findByEmailVerifiedTrue();

    List<User> findByEmailVerifiedFalse();

    List<User> findByUsernameContainingIgnoreCase(String keyword);

    List<User> findByFullNameContainingIgnoreCase(String keyword);

    List<User> findByEmailContainingIgnoreCase(String keyword);

    /* ==========================================================
                            THỐNG KÊ
       ========================================================== */

    long countByStatus(UserStatus status);

    long countByRole_Name(String roleName);
}