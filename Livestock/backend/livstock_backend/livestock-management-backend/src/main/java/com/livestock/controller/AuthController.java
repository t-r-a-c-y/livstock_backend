package com.livestock.controller;

import com.livestock.dto.ApiResponse;
import com.livestock.dto.LoginRequestDto;
import com.livestock.dto.LoginResponseDto;
import com.livestock.dto.RegisterRequestDto;
import com.livestock.dto.UserDto;
import com.livestock.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponseDto>> login(@Valid @RequestBody LoginRequestDto request) {
        LoginResponseDto response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success(response, "Login successful"));
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserDto>> register(@Valid @RequestBody RegisterRequestDto request) {
        // In real app: check if caller is admin or allow self-registration with default role
        UserDto created = authService.register(request, null); // or pass current admin ID
        return ResponseEntity.ok(ApiResponse.success(created, "User registered"));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<Void>> forgotPassword(@RequestParam String email) {
        authService.initiatePasswordReset(email);
        return ResponseEntity.ok(ApiResponse.success(null, "Password reset link sent"));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<Void>> resetPassword(
            @RequestParam String token,
            @RequestParam String newPassword) {

        authService.resetPassword(token, newPassword);
        return ResponseEntity.ok(ApiResponse.success(null, "Password reset successfully"));
    }
}