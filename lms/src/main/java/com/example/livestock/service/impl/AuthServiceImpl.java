package com.example.livestock.service.impl;

import com.example.livestock.dto.AuthRequest;
import com.example.livestock.dto.AuthResponse;
import com.example.livestock.dto.ApiMessageResponse;
import com.example.livestock.dto.LoginOtpVerifyRequest;
import com.example.livestock.dto.OtpRequest;
import com.example.livestock.repository.UserRepository;
import com.example.livestock.security.JwtService;
import com.example.livestock.security.UserPrincipal;
import com.example.livestock.service.AuthService;
import com.example.livestock.service.OtpService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final OtpService otpService;
    private final UserDetailsService userDetailsService;

    @Override
    public ApiMessageResponse login(AuthRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.email(), request.password()));
        otpService.sendLoginOtp(request.email());
        return new ApiMessageResponse("Password verified. OTP sent to email and development terminal.");
    }

    @Override
    public AuthResponse verifyLoginOtp(LoginOtpVerifyRequest request) {
        otpService.verifyLoginOtp(new com.example.livestock.dto.OtpVerifyRequest(request.email(), request.code()));
        UserPrincipal principal = (UserPrincipal) userDetailsService.loadUserByUsername(request.email());
        var user = principal.user();
        return new AuthResponse(jwtService.generateToken(principal), user.getId(), user.getFullName(), user.getEmail(), user.getRole());
    }
}
