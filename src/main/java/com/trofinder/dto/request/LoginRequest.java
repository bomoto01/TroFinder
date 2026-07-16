package com.trofinder.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    @NotBlank(message = "Tên đăng nhập hoặc email không được để trống")
    private String login;

    @NotBlank(message = "Mật khẩu không được để trống")
    private String password;

}