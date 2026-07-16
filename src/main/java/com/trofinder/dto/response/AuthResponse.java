package com.trofinder.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    /**
     * Access Token dùng để gọi API.
     */
    private String accessToken;

    /**
     * Refresh Token dùng để lấy Access Token mới.
     */
    private String refreshToken;

    /**
     * Loại token.
     * Thường luôn là "Bearer".
     */
    @Builder.Default
    private String tokenType = "Bearer";

    /**
     * Thời gian hết hạn của Access Token (giây).
     */
    private Long expiresIn;

    /**
     * Thông tin người dùng.
     */
    private UserResponse user;
    /**
     * Thông báo trả về
     */
    private String message;

}