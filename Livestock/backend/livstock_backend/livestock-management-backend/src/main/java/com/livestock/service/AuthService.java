// src/main/java/com/livestock/service/AuthService.java
package com.livestock.service;

import com.livestock.dto.request.LoginRequest;
import com.livestock.dto.response.LoginResponse;
import com.livestock.entity.User;
import com.livestock.exception.ResourceNotFoundException;
import com.livestock.exception.UnauthorizedException;
import com.livestock.repository.UserRepository;
import com.livestock.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final ModelMapper modelMapper;

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmailAndDeletedAtIsNull(request.getEmail())
                .orElseThrow(() -> new UnauthorizedException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new UnauthorizedException("Invalid email or password");
        }

        if (!"active".equals(user.getStatus())) {
            throw new UnauthorizedException("Account is inactive");
        }

        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);

        String token = jwtTokenProvider.generateToken(user.getEmail(), user.getRole());

        LoginResponse response = modelMapper.map(user, LoginResponse.class);
        response.setToken(token);
        return response;
    }
}