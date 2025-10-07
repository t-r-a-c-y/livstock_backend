package com.livestock.backend.service;


import com.livestock.backend.dto.request.UserRequest;
import com.livestock.backend.dto.response.UserResponse;
import com.livestock.backend.exception.ResourceNotFoundException;
import com.livestock.backend.model.UserProfile;
import com.livestock.backend.repository.UserProfileRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.passwordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserService {

    private final UserProfileRepository userProfileRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    public UserService(UserProfileRepository userProfileRepository, PasswordEncoder passwordEncoder, UserMapper userMapper) {
        this.userProfileRepository = userProfileRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
    }

    @PreAuthorize("hasRole('ADMIN')")
    public Page<UserResponse> getAllUsers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return userProfileRepository.findAll(pageable).map(userMapper::toResponse);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse createUser(UserRequest request) {
        if (userProfileRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }
        UserProfile user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user = userProfileRepository.save(user);
        return userMapper.toResponse(user);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse updateUser(UUID id, UserRequest request) {
        UserProfile user = userProfileRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setName(request.getName());
        user.setPhone(request.getPhone());
        user.setRole(request.getRole());
        user.setStatus("Active"); // Or from request
        user = userProfileRepository.save(user);
        return userMapper.toResponse(user);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void deleteUser(UUID id) {
        userProfileRepository.deleteById(id);
    }
}
