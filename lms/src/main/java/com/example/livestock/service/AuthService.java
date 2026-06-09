package com.example.livestock.service;

import com.example.livestock.dto.AuthRequest;
import com.example.livestock.dto.AuthResponse;

public interface AuthService {
    AuthResponse login(AuthRequest request);
}
