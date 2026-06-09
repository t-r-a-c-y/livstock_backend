package com.example.livestock.service;

import com.example.livestock.dto.AuthRequest;
import com.example.livestock.dto.AuthResponse;
import com.example.livestock.dto.ApiMessageResponse;
import com.example.livestock.dto.LoginOtpVerifyRequest;

public interface AuthService {
    ApiMessageResponse login(AuthRequest request);
    AuthResponse verifyLoginOtp(LoginOtpVerifyRequest request);
}
