package com.trofinder.service.impl;

// Import Entity
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.trofinder.entity.Role;
import com.trofinder.repository.RoleRepository;
import com.trofinder.service.interfaces.RoleService;

/*
 * @Service
 * Đánh dấu đây là tầng xử lý nghiệp vụ.
 */
@Service
public class RoleServiceImpl implements RoleService {

    /*
     * Tiêm RoleRepository vào Service.
     *
     * Spring sẽ tự tạo đối tượng RoleRepository.
     */
    @Autowired
    private RoleRepository roleRepository;

    /*
     * Override phương thức đã khai báo trong Interface.
     */
    @Override
    public List<Role> getAllRoles() {

        /*
         * findAll()
         * Là hàm có sẵn của JpaRepository.
         *
         * Nó tương đương SQL:
         *
         * SELECT * FROM roles;
         */
        return roleRepository.findAll();
    }
}