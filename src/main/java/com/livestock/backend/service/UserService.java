package com.livestock.backend.service;

import com.livestock.backend.dto.UserCreateDTO;
import com.livestock.backend.dto.UserDTO;
import com.livestock.backend.model.AuthUser;
import com.livestock.backend.repository.AuthUserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private AuthUserRepository authUserRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public Page<UserDTO> getAll(Pageable pageable) {
        logger.info("Fetching all users");
        Specification<AuthUser> spec = (root, query, cb) -> cb.isNull(root.get("deletedAt"));
        return authUserRepository.findAll(spec, pageable).map(this::toDTO);
    }

    @Transactional(readOnly = true)
    public UserDTO getById(UUID id) {
        logger.info("Fetching user by ID: {}", id);
        AuthUser user = authUserRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        if (user.getDeletedAt() != null) throw new RuntimeException("User deleted");
        return toDTO(user);
    }

    @Transactional
    public UserDTO create(UserCreateDTO dto) {
        logger.info("Creating user: {}", dto.getEmail());
        if (authUserRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }
        AuthUser user = new AuthUser();
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setName(dto.getName());
        user.setRole(dto.getRole());
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        user = authUserRepository.save(user);
        return toDTO(user);
    }

    @Transactional
    public UserDTO update(UUID id, UserDTO dto) {
        logger.info("Updating user: {}", id);
        AuthUser user = authUserRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        if (user.getDeletedAt() != null) throw new RuntimeException("User deleted");
        user.setEmail(dto.getEmail());
        user.setName(dto.getName());
        user.setRole(dto.getRole());
        user.setUpdatedAt(LocalDateTime.now());
        user = authUserRepository.save(user);
        return toDTO(user);
    }

    @Transactional
    public void softDelete(UUID id) {
        logger.info("Soft deleting user: {}", id);
        AuthUser user = authUserRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        user.setDeletedAt(LocalDateTime.now());
        authUserRepository.save(user);
    }

    @Transactional(readOnly = true)
    public UserDTO getByEmail(String email) {
        logger.info("Fetching user by email: {}", email);
        AuthUser user = authUserRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (user.getDeletedAt() != null) throw new RuntimeException("User deleted");
        return toDTO(user);
    }

    private UserDTO toDTO(AuthUser user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setName(user.getName());
        dto.setRole(user.getRole());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());
        return dto;
    }
}