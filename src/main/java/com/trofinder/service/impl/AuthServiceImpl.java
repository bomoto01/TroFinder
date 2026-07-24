package com.trofinder.service.impl;

import java.time.LocalDateTime;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    // ==========================================================
    // REGISTER
    // ==========================================================

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {

        validateRegister(request);

        Role role = getRole(request.getRole());

        User user = buildUser(request, role);

        user = userRepository.save(user);

        return buildResponse(user);
    }

    // ==========================================================
    // LOGIN
    // ==========================================================

    @Override
    @Transactional
    public AuthResponse login(LoginRequest request) {

        User user = userRepository.findByLogin(request.getLogin())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Tài khoản không tồn tại."));

        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPassword())) {

            throw new BadRequestException(
                    "Sai mật khẩu.");
        }

        if (Boolean.TRUE.equals(user.getDeleted())) {

            throw new BadRequestException(
                    "Tài khoản đã bị xóa.");
        }

        if (user.getStatus() == UserStatus.BLOCKED) {

            throw new BadRequestException(
                    "Tài khoản đã bị khóa.");

        }

        if (user.getStatus() == UserStatus.INACTIVE) {

            throw new BadRequestException(
                    "Tài khoản đang bị vô hiệu hóa.");

        }

        user.setLastActiveAt(LocalDateTime.now());

        userRepository.save(user);

        String accessToken =
                jwtService.generateAccessToken(
                        user.getUsername());

        String refreshToken =
                jwtService.generateRefreshToken(
                        user.getUsername());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(86400L)
                .user(userMapper.toResponse(user))
                .build();
    }

    // ==========================================================
    // PRIVATE
    // ==========================================================

    /**
     * Kiểm tra dữ liệu đăng ký
     */
    private void validateRegister(RegisterRequest request) {

        if (!request.getPassword()
                .equals(request.getConfirmPassword())) {

            throw new BadRequestException(
                    "Mật khẩu xác nhận không khớp.");
        }

        if (userRepository.existsByUsername(
                request.getUsername())) {

            throw new DuplicateResourceException(
                    "Tên đăng nhập đã tồn tại.");
        }

        if (request.getEmail() != null
                && userRepository.existsByEmail(request.getEmail())) {

            throw new DuplicateResourceException(
                    "Email đã tồn tại.");
        }

        if (userRepository.existsByPhone(
                request.getPhone())) {

            throw new DuplicateResourceException(
                    "Số điện thoại đã tồn tại.");
        }

    }

    /**
     * Lấy Role theo tên
     */
        private Role getRole(String roleName) {

        return roleRepository.findByName(roleName.toUpperCase())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Không tìm thấy Role: " + roleName));

        }

    /**
     * Tạo User
     */
    private User buildUser(
            RegisterRequest request,
            Role role) {

        User user = userMapper.toEntity(request);

        user.setPassword(
                passwordEncoder.encode(
                        request.getPassword()));

        user.setRole(role);

        user.setProvider(Provider.LOCAL);

        user.setStatus(UserStatus.PENDING);

        user.setEmailVerified(false);

        user.setPhoneVerified(false);

        user.setDeleted(false);

        user.setAvatar(null);

        user.setZaloPhone(null);

        user.setReputationScore(100);

        user.setAverageRating(0.0);

        user.setTotalReviews(0);

        user.setResponseRate(100.0);

        user.setNoShowCount((short) 0);

        user.setLastActiveAt(null);

        return user;
    }

    /**
     * Trả về kết quả đăng ký
     */
    private AuthResponse buildResponse(User user) {

        return AuthResponse.builder()
                .message("Đăng ký thành công")
                .user(userMapper.toResponse(user))
                .build();

    }

}