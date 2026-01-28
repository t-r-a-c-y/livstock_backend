package com.livestock.service;

import com.livestock.dto.LoginRequestDto;
import com.livestock.dto.LoginResponseDto;
import com.livestock.dto.RegisterRequestDto;
import com.livestock.dto.UserDto;
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
     * Authenticates a user and returns a JWT token along with user details.
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

        return response;
    }

    /**
     * Registers a new user.
     * - Encodes the plain password
     * - Checks for email uniqueness
     * - Uses default role STAFF if none provided
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

        // Encode password **once** here
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        // Determine role (default to STAFF if not provided)
        Role role = request.getRole() != null ? request.getRole() : Role.STAFF;

        // Create user via UserService (pass already encoded password)
        return userService.createUser(
                request.getEmail(),
                encodedPassword,          // ← already encoded
                request.getFirstName(),
                request.getLastName(),
                role,
                request.getPhone(),
                null                      // createdById = null for self-registration
        );
    }

    /**
     * Initiates password reset by generating a token and (later) sending email.
     */
    @Transactional
    public void initiatePasswordReset(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("No account found with this email"));

        String resetToken = UUID.randomUUID().toString();
        LocalDateTime expires = LocalDateTime.now().plusHours(2);

        userService.setPasswordResetToken(user.getId(), resetToken, expires);

        // TODO: Send email with reset link
        // Example: notificationService.sendPasswordResetEmail(user.getEmail(), resetToken);
    }

    /**
     * Completes password reset using the provided token.
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

        // Update password (encode it)
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        user.setPasswordResetToken(null);
        user.setPasswordResetExpires(null);

        userRepository.save(user);
    }
}