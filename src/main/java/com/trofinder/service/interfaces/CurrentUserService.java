package com.trofinder.service.interfaces;

import com.trofinder.entity.User;

public interface CurrentUserService {

    /**
     * Lấy User đang đăng nhập.
     */
    User getCurrentUser();

}