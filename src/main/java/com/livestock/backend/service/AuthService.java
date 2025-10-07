package com.livestock.backend.service;

import com.livestock.backend.model.UserProfile;
import com.livestock.backend.model.UserRole;
import com.livestock.backend.repository.UserProfileRepository;
import com.livestock.backend.util.JwtUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.passwordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserProfileRepository userProfileRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    public AuthService(UserProfileRepository userProfileRepository, PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager, JwtUtils jwtUtils) {
        this.userProfileRepository = userProfileRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
    }

    public UserProfile signup(String email, String password, String name, String phone, UserRole role) {
        if (userProfileRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("Email already exists");
        }
        UserProfile user = new UserProfile();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setName(name);
        user.setPhone(phone);
        user.setRole(role != null ? role : UserRole.MANAGER);
        return userProfileRepository.save(user);
    }

    public String login(String email, String password) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return jwtUtils.generateJwtToken(authentication);
    }

    public UserProfile getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth.getPrincipal() instanceof UserProfile) {
            return (UserProfile) auth.getPrincipal();
        }
        throw new RuntimeException("No current user");
    }
}