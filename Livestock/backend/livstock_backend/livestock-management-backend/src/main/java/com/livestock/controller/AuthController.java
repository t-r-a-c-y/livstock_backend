// src/main/java/com/livestock/controller/AuthController.java
package com.livestock.controller;

import com.livestock.dto.request.LoginRequest;
import com.livestock.dto.response.ApiResponse;
import com.livestock.dto.response.LoginResponse;
import com.livestock.security.JwtTokenProvider;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthController(AuthenticationManager authenticationManager,
                          JwtTokenProvider jwtTokenProvider) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String token = jwtTokenProvider.generateToken(userDetails);

            LoginResponse loginResponse = new LoginResponse(token);

            return ResponseEntity.ok(ApiResponse.success(loginResponse));

        } catch (BadCredentialsException e) {
            return ResponseEntity.status(401)
                    .body(ApiResponse.error("Invalid email or password", "UNAUTHORIZED"));
        } catch (DisabledException e) {
            return ResponseEntity.status(401)
                    .body(ApiResponse.error("User account is disabled", "DISABLED"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500)
                    .body(ApiResponse.error("Server error: " + e.getMessage(), "SERVER_ERROR"));
        }
    }
}