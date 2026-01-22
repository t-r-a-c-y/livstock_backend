package com.livestock.service;

import com.livestock.dto.UserDto;
import com.livestock.entity.User;
import com.livestock.entity.enums.Role;
import com.livestock.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserDto createUser(
            String email,
            String rawPassword,
            String firstName,
            String lastName,
            Role role,
            String phone,
            UUID createdById) {

        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already registered");
        }

        User user = new User();
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(rawPassword));
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setRole(role);
        user.setPhone(phone);
        user.setActive(true);
        user.setEmailVerified(false);

        if (createdById != null) {
            user.setCreatedBy(userRepository.findById(createdById).orElse(null));
        }

        User saved = userRepository.save(user);
        return mapToDto(saved);
    }

    @Transactional
    public UserDto updateUser(UUID id, UserDto dto) {
        User existing = getUserEntity(id);

        if (!existing.getEmail().equals(dto.getEmail()) &&
                userRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Email already in use");
        }

        existing.setEmail(dto.getEmail());
        existing.setFirstName(dto.getFirstName());
        existing.setLastName(dto.getLastName());
        existing.setRole(dto.getRole());
        existing.setPhone(dto.getPhone());
        existing.setAvatar(dto.getAvatar());

        // Password update is handled separately (see changePassword)
        User saved = userRepository.save(existing);
        return mapToDto(saved);
    }

    @Transactional(readOnly = true)
    public UserDto getUserById(UUID id) {
        return mapToDto(getUserEntity(id));
    }

    @Transactional(readOnly = true)
    public User getUserEntity(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + id));
    }

    @Transactional(readOnly = true)
    public UserDto getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
        return mapToDto(user);
    }

    @Transactional(readOnly = true)
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void activateUser(UUID id) {
        User user = getUserEntity(id);
        user.setActive(true);
        userRepository.save(user);
    }

    @Transactional
    public void deactivateUser(UUID id) {
        User user = getUserEntity(id);
        user.setActive(false);
        userRepository.save(user);
    }

    @Transactional
    public void changePassword(UUID id, String oldPassword, String newPassword) {
        User user = getUserEntity(id);

        if (!passwordEncoder.matches(oldPassword, user.getPasswordHash())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }

        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    // Called by AuthService for reset flow
    @Transactional
    public void setPasswordResetToken(UUID userId, String token, LocalDateTime expires) {
        User user = getUserEntity(userId);
        user.setPasswordResetToken(token);
        user.setPasswordResetExpires(expires);
        userRepository.save(user);
    }

    // Public mapping method - used by AuthService too
    public UserDto mapToDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setRole(user.getRole());
        dto.setActive(user.isActive());
        dto.setAvatar(user.getAvatar());
        dto.setPhone(user.getPhone());
        dto.setLastLogin(user.getLastLogin());
        dto.setEmailVerified(user.isEmailVerified());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());
        return dto;
    }
}