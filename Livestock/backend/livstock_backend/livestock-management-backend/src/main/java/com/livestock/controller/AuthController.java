// src/main/java/com/livestock/controller/AuthController.java
package com.livestock.controller;

import com.livestock.dto.request.LoginRequest;
import com.livestock.dto.response.ApiResponse;
import com.livestock.dto.response.LoginResponse;
import com.livestock.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    // Explicit constructor — fixes "not initialized" error
    public AuthController(AuthService authService) {
        this.authService = authService;
    }
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);

            User user = (User) authentication.getPrincipal();
            String token = jwtTokenProvider.generateToken(user);

            LoginResponse response = new LoginResponse(token);
            return ResponseEntity.ok(ApiResponse.success(response));

        } catch (BadCredentialsException e) {
            return ResponseEntity.status(401).body(ApiResponse.error("Invalid email or password"));
        } catch (DisabledException e) {
            return ResponseEntity.status(401).body(ApiResponse.error("User account is inactive"));
        } catch (Exception e) {
            e.printStackTrace();  // This will show the real error in console
            return ResponseEntity.status(500).body(ApiResponse.error("Server error: " + e.getMessage()));
        }
    }


}