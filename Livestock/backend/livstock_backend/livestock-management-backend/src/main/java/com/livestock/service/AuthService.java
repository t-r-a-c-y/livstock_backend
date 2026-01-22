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
    private final UserRepository userRepository;           // ← added
    private final PasswordEncoder passwordEncoder;         // ← added
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public LoginResponseDto login(LoginRequestDto request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = jwtUtil.generateToken(authentication);

        // Get user details
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found after authentication"));

        UserDto userDto = userService.mapToDto(user);

        LoginResponseDto response = new LoginResponseDto();
        response.setToken(token);
        response.setUser(userDto);

        return response;
    }

    @Transactional
    public UserDto register(RegisterRequestDto request) {
        Role role = request.getRole() != null ? request.getRole() : Role.STAFF;

        // No extra null parameter anymore
        return userService.createUser(
                request.getEmail(),
                request.getPassword(),
                request.getFirstName(),
                request.getLastName(),
                role,
                request.getPhone(),
                null  // createdById = null for self-registration
        );
    }

    @Transactional
    public void initiatePasswordReset(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Email not found"));

        String resetToken = UUID.randomUUID().toString();
        LocalDateTime expires = LocalDateTime.now().plusHours(2);

        userService.setPasswordResetToken(user.getId(), resetToken, expires);

        // TODO: send email with reset link (implement in NotificationService)
    }

    @Transactional
    public void resetPassword(String token, String newPassword) {
        User user = userRepository.findByPasswordResetToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Invalid or expired reset token"));

        if (user.getPasswordResetExpires() == null || user.getPasswordResetExpires().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Reset token expired");
        }

        user.setPasswordHash(passwordEncoder.encode(newPassword));
        user.setPasswordResetToken(null);
        user.setPasswordResetExpires(null);
        userRepository.save(user);
    }
}