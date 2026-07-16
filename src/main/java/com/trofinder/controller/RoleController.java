package com.trofinder.controller; // Package chứa các REST API

// Import Entity
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.trofinder.entity.Role;
import com.trofinder.service.interfaces.RoleService;

/*
 * @RestController
 *
 * Đánh dấu đây là REST Controller.
 *
 * Spring sẽ tự động chuyển Object Java
 * thành JSON để trả về Client.
 */
@RestController

/*
 * Tất cả API trong class này
 * đều bắt đầu bằng:
 *
 * http://localhost:8080/api/roles
 */
@RequestMapping("/api/roles")
public class RoleController {

    /*
     * Tiêm RoleService vào Controller.
     */
    @Autowired
    private RoleService roleService;

    /*
     * @GetMapping
     *
     * Khi Client gửi:
     *
     * GET /api/roles
     *
     * Spring sẽ gọi hàm này.
     */
    @GetMapping
    public List<Role> getAllRoles() {

        /*
         * Gọi Service lấy toàn bộ Role
         * rồi trả về Client.
         */
        return roleService.getAllRoles();
    }

}