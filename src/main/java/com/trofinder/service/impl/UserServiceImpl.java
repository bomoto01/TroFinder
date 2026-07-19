package com.trofinder.service.impl;

import java.time.LocalDateTime;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.trofinder.dto.response.UserResponse;
import com.trofinder.entity.User;
import com.trofinder.exception.ResourceNotFoundException;
import com.trofinder.mapper.UserMapper;
import com.trofinder.repository.UserRepository;
import com.trofinder.service.interfaces.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    /**
     * Lấy thông tin người dùng đang đăng nhập
     */
    @Override
    @Transactional
    public UserResponse getCurrentUser() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        String username = authentication.getName();

        User user = userRepository
                .findByUsername(username)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Không tìm thấy người dùng."));

        // Cập nhật thời gian hoạt động gần nhất
        user.setLastActiveAt(LocalDateTime.now());

        return userMapper.toResponse(user);
    }

}