package com.trofinder.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.trofinder.entity.Role;
import com.trofinder.repository.RoleRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;

    @Override
    public void run(String... args) {

        createRole("ADMIN", "Quản trị viên");

        createRole("LANDLORD", "Chủ trọ");

        createRole("TENANT", "Người thuê");

    }

    private void createRole(String name, String description) {

        if (roleRepository.findByName(name).isEmpty()) {

            Role role = new Role();

            role.setName(name);

            role.setDescription(description);

            roleRepository.save(role);

            System.out.println("Đã tạo Role: " + name);

        }

    }

}