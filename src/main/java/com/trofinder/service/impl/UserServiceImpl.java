package com.trofinder.service.impl;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

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

    @Override
    public UserResponse getCurrentUser() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String username = authentication.getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Không tìm thấy người dùng"));

        return userMapper.toResponse(user);
    }

}