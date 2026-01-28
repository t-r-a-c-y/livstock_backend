package com.livestock.controller;

import com.livestock.dto.*;
import com.livestock.entity.User;
import com.livestock.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final PasswordEncoder passwordEncoder;

    /**
     * Authenticate user and return JWT token + user info.
     * Includes mustChangePassword flag for first-time login (e.g., auto-created owners).
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponseDto>> login(@Valid @RequestBody LoginRequestDto request) {
        LoginResponseDto response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success(response, "Login successful"));
    }

    /**
     * Register a new user (manual/staff registration).
     * Owners are auto-created via OwnerService — not here.
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserDto>> register(@Valid @RequestBody RegisterRequestDto request) {
        UserDto created = authService.register(request);
        return ResponseEntity.ok(ApiResponse.success(created, "User registered successfully"));
    }

    /**
     * Initiate forgot-password flow (send reset token/link).
     */
    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<Void>> forgotPassword(@RequestParam String email) {
        authService.initiatePasswordReset(email);
        return ResponseEntity.ok(ApiResponse.success(null, "Password reset link sent to your email"));
    }

    /**
     * Reset password using a valid reset token.
     */
    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<Void>> resetPassword(
            @RequestParam String token,
            @RequestParam String newPassword) {

        authService.resetPassword(token, newPassword);
        return ResponseEntity.ok(ApiResponse.success(null, "Password reset successfully"));
    }

    /**
     * Endpoint for forced password change on first login.
     * Called after login when mustChangePassword = true.
     */
    @PostMapping("/change-password-first")
    public ResponseEntity<ApiResponse<Void>> changePasswordFirst(
            @Valid @RequestBody ChangePasswordFirstDto dto,
            @AuthenticationPrincipal User user) {

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Unauthorized - please log in first", "UNAUTHENTICATED"));
        }

        if (!user.isMustChangePassword()) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Password change is not required for this account", "NO_CHANGE_NEEDED"));
        }

        // Verify current password
        if (!passwordEncoder.matches(dto.getCurrentPassword(), user.getPasswordHash())) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Current password is incorrect", "INVALID_CREDENTIALS"));
        }

        // Validate new password
        if (dto.getNewPassword() == null || dto.getNewPassword().length() < 8) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("New password must be at least 8 characters long", "WEAK_PASSWORD"));
        }

        authService.changePasswordOnFirstLogin(dto, user.getEmail());

        return ResponseEntity.ok(
                ApiResponse.success(null, "Password updated successfully. Please log in again.")
        );
    }
}