package com.trofinder.service.interfaces;

import com.trofinder.dto.response.UserResponse;

public interface UserService {

    /**
     * Lấy thông tin người dùng đang đăng nhập
     */
    UserResponse getCurrentUser();

}