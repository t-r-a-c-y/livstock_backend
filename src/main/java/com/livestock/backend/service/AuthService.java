package com.livestock.backend.service;

import com.livestock.backend.dto.AuthRequestDTO;
import com.livestock.backend.dto.AuthResponseDTO;
import com.livestock.backend.model.Profile;
import com.livestock.backend.model.UserRole;
import com.livestock.backend.repository.ProfileRepository;
import com.livestock.backend.repository.UserRoleRepository;
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
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public AuthResponseDTO login(AuthRequestDTO authRequest) {
        logger.info("Attempting login for user: {}", authRequest.getEmail());
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword()));
        String token = jwtTokenProvider.generateToken(authentication);
        AuthResponseDTO response = new AuthResponseDTO();
        response.setToken(token);
        response.setRefreshToken("refresh-" + UUID.randomUUID()); // Simplified refresh token
        return response;
    }

    @Transactional
    public AuthResponseDTO register(AuthRequestDTO authRequest) {
        logger.info("Registering new user: {}", authRequest.getEmail());
        if (profileRepository.findAll().stream().anyMatch(p -> authRequest.getEmail().equals(p.getName()))) {
            throw new RuntimeException("Email already exists");
        }

        Profile profile = new Profile();
        profile.setId(UUID.randomUUID());
        profile.setName(authRequest.getName());
        profile.setPhone(null);
        profile.setAvatar(null);
        profile.setCreatedAt(LocalDateTime.now());
        profile.setUpdatedAt(LocalDateTime.now());
        profileRepository.save(profile);

        UserRole role = new UserRole();
        role.setUserId(profile.getId());
        role.setRole(authRequest.getRole() != null ? authRequest.getRole() : "USER");
        role.setCreatedAt(LocalDateTime.now());
        userRoleRepository.save(role);

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword()));
        String token = jwtTokenProvider.generateToken(authentication);
        AuthResponseDTO response = new AuthResponseDTO();
        response.setToken(token);
        response.setRefreshToken("refresh-" + UUID.randomUUID());
        return response;
    }
}