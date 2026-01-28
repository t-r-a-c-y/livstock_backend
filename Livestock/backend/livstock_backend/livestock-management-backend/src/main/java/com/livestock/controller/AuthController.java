package com.livestock.controller;

import com.livestock.dto.*;
import com.livestock.entity.User;
import com.livestock.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.password.PasswordEncoder;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final PasswordEncoder passwordEncoder;  // if needed directly in controller (optional)

    /**
     * Authenticates a user and returns JWT + user details
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponseDto>> login(@Valid @RequestBody LoginRequestDto request) {
        LoginResponseDto response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success(response, "Login successful"));
    }

    /**
     * Registers a new user (manual/staff registration)
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserDto>> register(@Valid @RequestBody RegisterRequestDto request) {
        UserDto created = authService.register(request);
        return ResponseEntity.ok(ApiResponse.success(created, "User registered successfully"));
    }

    /**
     * Initiates forgot password flow (sends reset link/token)
     */
    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<Void>> forgotPassword(@RequestParam String email) {
        authService.initiatePasswordReset(email);
        return ResponseEntity.ok(ApiResponse.success(null, "Password reset link sent to email"));
    }

    /**
     * Completes password reset using the token
     */
    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<Void>> resetPassword(
            @RequestParam String token,
            @RequestParam String newPassword) {
        authService.resetPassword(token, newPassword);
        return ResponseEntity.ok(ApiResponse.success(null, "Password reset successfully"));
    }

    /**
     * Endpoint called when user must change password on first login
     * (triggered after login when mustChangePassword = true)
     */
    @PostMapping("/change-password-first")
    public ResponseEntity<ApiResponse<Void>> changePasswordFirst(
            @Valid @RequestBody ChangePasswordFirstDto dto,
            @AuthenticationPrincipal User user) {   // ← cleaner than Authentication + cast

        // Verify this is a first-login scenario
        if (!user.isMustChangePassword()) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Password change is not required for this account"));
        }

        // Validate current (temporary) password
        if (!passwordEncoder.matches(dto.getCurrentPassword(), user.getPasswordHash())) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Current password is incorrect"));
        }

        // Validate new password
        if (dto.getNewPassword() == null || dto.getNewPassword().length() < 8) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("New password must be at least 8 characters long"));
        }

        // Perform the change via service
        authService.changePasswordOnFirstLogin(dto, user.getEmail());

        return ResponseEntity.ok(ApiResponse.success(null,
                "Password updated successfully. Please log in again with your new password."));
    }
}