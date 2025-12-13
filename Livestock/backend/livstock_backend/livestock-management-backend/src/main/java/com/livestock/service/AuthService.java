// src/main/java/com/livestock/service/AuthService.java
package com.livestock.service;

import com.livestock.dto.request.LoginRequest;
import com.livestock.dto.response.LoginResponse;
import com.livestock.entity.User;
import com.livestock.exception.UnauthorizedException;
import com.livestock.repository.UserRepository;
import com.livestock.security.JwtTokenProvider;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final ModelMapper modelMapper;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtTokenProvider jwtTokenProvider,
                       ModelMapper modelMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.modelMapper = modelMapper;
    }

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

        // Create a mock Authentication object with role
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().toUpperCase()));
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                user.getEmail(), null, authorities);

        String token = jwtTokenProvider.generateToken(authentication);

        LoginResponse response = modelMapper.map(user, LoginResponse.class);
        response.setToken(token);
        return response;
    }
}