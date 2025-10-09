package com.livestock.backend.service;

import com.livestock.backend.dto.JwtResponse;
import com.livestock.backend.dto.LoginRequest;
import com.livestock.backend.dto.RegisterRequest;
import com.livestock.backend.model.AuthUser;
import com.livestock.backend.repository.AuthUserRepository;
import com.livestock.backend.security.JwtTokenProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AuthService {
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private AuthUserRepository authUserRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Transactional
    public JwtResponse login(LoginRequest loginRequest) {
        logger.info("Processing login for user: {}", loginRequest.getEmail());
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
        );
        AuthUser user = authUserRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (user.getDeletedAt() != null) {
            throw new RuntimeException("User account is deleted");
        }
        String jwt = tokenProvider.generateToken(authentication);
        JwtResponse response = new JwtResponse();
        response.setToken(jwt);
        response.setId(user.getId());
        response.setEmail(user.getEmail());
        response.setName(user.getName());
        response.setRole(user.getRole());
        return response;
    }

    @Transactional
    public void register(RegisterRequest registerRequest) {
        logger.info("Registering new user: {}", registerRequest.getEmail());
        if (authUserRepository.findByEmail(registerRequest.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }
        AuthUser user = new AuthUser();
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setName(registerRequest.getName());
        user.setRole(registerRequest.getRole());
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        authUserRepository.save(user);
    }
}