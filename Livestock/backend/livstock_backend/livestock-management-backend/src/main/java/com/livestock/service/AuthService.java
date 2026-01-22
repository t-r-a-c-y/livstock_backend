package com.livestock.service;

import com.livestock.dto.LoginRequestDto;
import com.livestock.dto.LoginResponseDto;
import com.livestock.dto.RegisterRequestDto;
import com.livestock.dto.UserDto;
import com.livestock.entity.User;
import com.livestock.entity.enums.Role;
import com.livestock.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;
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

        User user = userService.getUserEntity(
                UUID.fromString(authentication.getName())  // assuming ID is used as principal
        );

        UserDto userDto = userService.mapToDto(user);

        LoginResponseDto response = new LoginResponseDto();
        response.setToken(token);
        response.setUser(userDto);

        return response;
    }

    @Transactional
    public UserDto register(RegisterRequestDto request) {
        // In production: restrict who can set role (usually admin)
        Role role = request.getRole() != null ? request.getRole() : Role.STAFF;

        return userService.createUser(
                request.getEmail(),
                request.getPassword(),
                request.getFirstName(),
                request.getLastName(),
                role,
                request.getPhone(),
                null  // self-registration - no creator, or get from context
        );
    }

    @Transactional
    public void initiatePasswordReset(String email) {
        User user = userService.getUserEntity(
                userService.getUserByEmail(email).getId()
        );

        String resetToken = UUID.randomUUID().toString();
        LocalDateTime expires = LocalDateTime.now().plusHours(2);

        userService.setPasswordResetToken(user.getId(), resetToken, expires);

        // TODO: send email with reset link containing token
        // e.g. notificationService.sendResetEmail(email, resetToken);
    }

    @Transactional
    public void resetPassword(String token, String newPassword) {
        User user = userRepository.findByPasswordResetToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Invalid or expired token"));

        if (user.getPasswordResetExpires() == null || user.getPasswordResetExpires().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Reset token has expired");
        }

        user.setPasswordHash(passwordEncoder.encode(newPassword));
        user.setPasswordResetToken(null);
        user.setPasswordResetExpires(null);

        userRepository.save(user);
    }
}