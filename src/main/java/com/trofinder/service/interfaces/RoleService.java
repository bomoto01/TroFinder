package com.trofinder.service.interfaces; // Package chứa interface của Service

// Import Entity Role
import com.trofinder.entity.Role;

// Import List để trả về danh sách Role
import java.util.List;

/*
 * Đây là Interface của RoleService.
 *
 * Interface chỉ khai báo các chức năng,
 * chưa viết cách thực hiện.
 */
public interface RoleService {

    /*
     * Lấy toàn bộ Role trong database.
     *
     * Ví dụ:
     * ADMIN
     * LANDLORD
     * TENANT
     */
    List<Role> getAllRoles();

}