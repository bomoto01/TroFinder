package com.trofinder.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.trofinder.entity.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    /**
     * Tìm Role theo tên.
     * Ví dụ:
     * ADMIN
     * TENANT
     * LANDLORD
     */
    Optional<Role> findByName(String name);

}