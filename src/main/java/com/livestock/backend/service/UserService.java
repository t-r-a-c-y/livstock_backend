package com.livestock.backend.service;

import com.livestock.backend.dto.request.UserRequest;
import com.livestock.backend.dto.response.UserResponse;
import com.livestock.backend.exception.ResourceNotFoundException;
import com.livestock.backend.model.UserProfile;
import com.livestock.backend.repository.UserProfileRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserService {

    private final UserProfileRepository userProfileRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserProfileRepository userProfileRepository, UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userProfileRepository = userProfileRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public UserResponse createUser(UserRequest userRequest) {
        UserProfile user = userMapper.toEntity(userRequest);
        user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        user = userProfileRepository.save(user);
        return userMapper.toResponse(user);
    }

    public UserResponse getUserById(UUID id) {
        UserProfile user = userProfileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return userMapper.toResponse(user);
    }

    public UserResponse updateUser(UUID id, UserRequest userRequest) {
        UserProfile user = userProfileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        user.setName(userRequest.getName());
        user.setEmail(userRequest.getEmail());
        if (userRequest.getPassword() != null && !userRequest.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        }
        user.setStatus(userRequest.getStatus());
        user = userProfileRepository.save(user);
        return userMapper.toResponse(user);
    }

    public void deleteUser(UUID id) {
        UserProfile user = userProfileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        userProfileRepository.delete(user);
    }
}