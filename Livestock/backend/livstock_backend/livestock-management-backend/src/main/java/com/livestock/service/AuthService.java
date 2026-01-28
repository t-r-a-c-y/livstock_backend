package com.livestock.service;

import com.livestock.dto.*;
import com.livestock.entity.User;
import com.livestock.entity.enums.Role;
import com.livestock.repository.UserRepository;
import com.livestock.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    /**
     * Authenticates a user and returns a JWT token + user details.
     * If this is the first login (mustChangePassword = true), the response will include
     * a flag telling the frontend to force a password change.
     */
    @Transactional(readOnly = true)
    public LoginResponseDto login(LoginRequestDto request) {
        // Authenticate with Spring Security
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        // Set authenticated user in security context
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Generate JWT token
        String token = jwtUtil.generateToken(authentication);

        // Fetch full user details
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found after successful authentication"));

        UserDto userDto = userService.mapToDto(user);

        // Build response
        LoginResponseDto response = new LoginResponseDto();
        response.setToken(token);
        response.setUser(userDto);
        response.setMustChangePassword(user.isMustChangePassword());  // ← new flag

        return response;
    }

    /**
     * Registers a new user (used for manual/staff registration).
     * Owners are created automatically via OwnerService — not here.
     */
    @Transactional
    public UserDto register(RegisterRequestDto request) {
        // Basic validation
        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        }
        if (request.getPassword() == null || request.getPassword().length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters long");
        }

        // Prevent duplicate registration
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email is already registered");
        }

        // Encode password once
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        // Default role = STAFF if not provided
        Role role = request.getRole() != null ? request.getRole() : Role.STAFF;

        // Create user (pass already encoded password)
        UserDto createdUser = userService.createUser(
                request.getEmail(),
                encodedPassword,
                request.getFirstName(),
                request.getLastName(),
                role,
                request.getPhone(),
                null  // createdById = null for self-registration
        );

        // For normal registrations, no forced password change
        return createdUser;
    }

    /**
     * Allows a user who has mustChangePassword=true to change their password on first login.
     * This is typically called after successful login when the frontend sees the flag.
     */
    @Transactional
    public void changePasswordOnFirstLogin(ChangePasswordFirstDto dto, String currentUserEmail) {
        User user = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Only allowed if mustChangePassword is true
        if (!user.isMustChangePassword()) {
            throw new IllegalStateException("Password change is not required for this account");
        }

        // Validate current (temporary) password
        if (!passwordEncoder.matches(dto.getCurrentPassword(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }

        // Validate new password strength
        if (dto.getNewPassword() == null || dto.getNewPassword().length() < 8) {
            throw new IllegalArgumentException("New password must be at least 8 characters long");
        }

        // Update password and reset flag
        user.setPasswordHash(passwordEncoder.encode(dto.getNewPassword()));
        user.setMustChangePassword(false);
        user.setUpdatedAt(LocalDateTime.now());

        userRepository.save(user);
    }

    /**
     * Initiates password reset (forgot password flow) — unchanged
     */
    @Transactional
    public void initiatePasswordReset(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("No account found with this email"));

        String resetToken = UUID.randomUUID().toString();
        LocalDateTime expires = LocalDateTime.now().plusHours(2);

        userService.setPasswordResetToken(user.getId(), resetToken, expires);

        // TODO: Send email with reset link
        // notificationService.sendPasswordResetEmail(user.getEmail(), resetToken);
    }

    /**
     * Completes password reset using the provided token — unchanged
     */
    @Transactional
    public void resetPassword(String token, String newPassword) {
        if (newPassword == null || newPassword.length() < 8) {
            throw new IllegalArgumentException("New password must be at least 8 characters long");
        }

        User user = userRepository.findByPasswordResetToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Invalid or expired reset token"));

        if (user.getPasswordResetExpires() == null ||
                user.getPasswordResetExpires().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Reset token has expired");
        }

        user.setPasswordHash(passwordEncoder.encode(newPassword));
        user.setPasswordResetToken(null);
        user.setPasswordResetExpires(null);
        user.setUpdatedAt(LocalDateTime.now());

        userRepository.save(user);
    }
}