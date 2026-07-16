package com.trofinder.service.interfaces;

import com.trofinder.dto.request.LoginRequest;
import com.trofinder.dto.request.RegisterRequest;
import com.trofinder.dto.response.AuthResponse;

public interface AuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

}