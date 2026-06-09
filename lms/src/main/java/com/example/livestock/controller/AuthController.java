package com.example.livestock.controller;

import com.example.livestock.dto.AuthRequest;
import com.example.livestock.dto.AuthResponse;
import com.example.livestock.dto.ApiMessageResponse;
import com.example.livestock.dto.OtpRequest;
import com.example.livestock.dto.OtpVerifyRequest;
import com.example.livestock.service.AuthService;
import com.example.livestock.service.OtpService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final OtpService otpService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/otp/request")
    public ResponseEntity<ApiMessageResponse> requestOtp(@Valid @RequestBody OtpRequest request) {
        otpService.sendOtp(request);
        return ResponseEntity.ok(new ApiMessageResponse("OTP sent to email"));
    }

    @PostMapping("/otp/verify")
    public ResponseEntity<ApiMessageResponse> verifyOtp(@Valid @RequestBody OtpVerifyRequest request) {
        otpService.verify(request);
        return ResponseEntity.ok(new ApiMessageResponse("OTP verified"));
    }
}
