package com.example.livestock.service.impl;

import com.example.livestock.dto.AuthRequest;
import com.example.livestock.dto.AuthResponse;
import com.example.livestock.security.JwtService;
import com.example.livestock.security.UserPrincipal;
import com.example.livestock.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Override
    public AuthResponse login(AuthRequest request) {
        var authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password()));
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        var user = principal.user();
        return new AuthResponse(jwtService.generateToken(principal), user.getId(), user.getFullName(), user.getEmail(), user.getRole());
    }
}
