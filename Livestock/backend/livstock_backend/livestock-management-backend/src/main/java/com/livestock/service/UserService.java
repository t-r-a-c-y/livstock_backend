// src/main/java/com/livestock/service/UserService.java
package com.livestock.service;

import com.livestock.dto.request.UserRequest;
import com.livestock.dto.response.UserResponse;
import com.livestock.entity.User;
import com.livestock.exception.ResourceNotFoundException;
import com.livestock.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, ModelMapper modelMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.modelMapper = modelMapper;
    }

    public List<UserResponse> getAllUsers() {
        return userRepository.findAllActive().stream()
                .map(u -> modelMapper.map(u, UserResponse.class))
                .toList();
    }

    public UserResponse getUserById(UUID id) {
        User user = userRepository.findActiveById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return modelMapper.map(user, UserResponse.class);
    }

    public UserResponse createUser(UserRequest request) {
        if (userRepository.existsByEmailAndDeletedAtIsNull(request.getEmail())) {
            throw new IllegalArgumentException("Email already in use");
        }

        User user = modelMapper.map(request, User.class);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setStatus("active");
        user = userRepository.save(user);

        return modelMapper.map(user, UserResponse.class);
    }

    public UserResponse updateUser(UUID id, UserRequest request) {
        User user = userRepository.findActiveById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!user.getEmail().equals(request.getEmail()) &&
                userRepository.existsByEmailAndDeletedAtIsNull(request.getEmail())) {
            throw new IllegalArgumentException("Email already in use");
        }

        modelMapper.map(request, user);
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        user.setUpdatedAt(LocalDateTime.now());
        user = userRepository.save(user);

        return modelMapper.map(user, UserResponse.class);
    }

    public void deleteUser(UUID id) {
        User user = userRepository.findActiveById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setDeletedAt(LocalDateTime.now());
        user.setStatus("inactive");
        userRepository.save(user);
    }

    public void deactivateUser(UUID id) {
        User user = userRepository.findActiveById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setStatus("inactive");
        userRepository.save(user);
    }

    public void activateUser(UUID id) {
        User user = userRepository.findActiveById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setStatus("active");
        userRepository.save(user);
    }
}