package com.trofinder.service.impl;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.trofinder.dto.request.LoginRequest;
import com.trofinder.dto.request.RegisterRequest;
import com.trofinder.dto.response.AuthResponse;
import com.trofinder.entity.Role;
import com.trofinder.entity.User;
import com.trofinder.enums.Provider;
import com.trofinder.enums.UserStatus;
import com.trofinder.exception.BadRequestException;
import com.trofinder.exception.DuplicateResourceException;
import com.trofinder.exception.ResourceNotFoundException;
import com.trofinder.mapper.UserMapper;
import com.trofinder.repository.RoleRepository;
import com.trofinder.repository.UserRepository;
import com.trofinder.security.jwt.JwtService;
import com.trofinder.service.interfaces.AuthService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;

    private final JwtService jwtService;

    private final RoleRepository roleRepository;

    private final UserMapper userMapper;

    private final PasswordEncoder passwordEncoder;

    @Override
    public AuthResponse register(RegisterRequest request) {

        validateRegister(request);

        Role role = getDefaultRole();

        User user = buildUser(request, role);

        user = userRepository.save(user);

        return buildResponse(user);
    }

    @Override
    public AuthResponse login(LoginRequest request) {

        User user = userRepository.findByLogin(request.getLogin())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Tài khoản không tồn tại."));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadRequestException("Sai mật khẩu.");
        }

        String accessToken = jwtService.generateAccessToken(user.getUsername());

        String refreshToken = jwtService.generateRefreshToken(user.getUsername());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(86400L)
                .user(userMapper.toResponse(user))
                .build();
    }

    /**
     * Kiểm tra dữ liệu đăng ký
     */
    private void validateRegister(RegisterRequest request) {

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new BadRequestException("Mật khẩu xác nhận không khớp.");
        }

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateResourceException("Tên đăng nhập đã tồn tại.");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email đã tồn tại.");
        }

        if (userRepository.existsByPhone(request.getPhone())) {
            throw new DuplicateResourceException("Số điện thoại đã tồn tại.");
        }

    }

    /**
     * Lấy Role mặc định khi đăng ký
     */
    private Role getDefaultRole() {

        return roleRepository.findByName("TENANT")
                .orElseThrow(() ->
                        new ResourceNotFoundException("Không tìm thấy Role TENANT"));

    }

    /**
     * Tạo User từ RegisterRequest
     */
    private User buildUser(RegisterRequest request, Role role) {

        User user = userMapper.toEntity(request);

        user.setPassword(passwordEncoder.encode(request.getPassword()));

        user.setRole(role);

        user.setProvider(Provider.LOCAL);

        user.setStatus(UserStatus.PENDING);

        user.setEmailVerified(false);

        user.setPhoneVerified(false);

        return user;
    }

    /**
     * Tạo AuthResponse
     */
    private AuthResponse buildResponse(User user) {

        return AuthResponse.builder()
                .message("Đăng ký thành công")
                .user(userMapper.toResponse(user))
                .build();

    }

}