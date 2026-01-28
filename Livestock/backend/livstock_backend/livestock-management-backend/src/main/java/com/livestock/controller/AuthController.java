package com.livestock.controller;

import com.livestock.dto.*;
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
        UserDto created = authService.register(request);   // ← fixed: only one argument
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
    @PostMapping("/change-password-first")
    public ResponseEntity<String> changePasswordFirst(
            @RequestBody ChangePasswordFirstDto dto,
            Authentication authentication) {
        String currentUserEmail = authentication.getName(); // or from principal
        authService.changePasswordOnFirstLogin(dto, currentUserEmail);
        return ResponseEntity.ok("Password changed successfully. Please log in again.");
    }
}