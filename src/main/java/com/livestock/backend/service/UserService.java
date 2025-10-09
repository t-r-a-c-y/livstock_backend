package com.livestock.backend.service;

import com.livestock.backend.dto.UserCreateDTO;
import com.livestock.backend..dto.UserDTO;
import com.livestock.backend.model.AuthUser;
import com.livestock.backend.model.Profile;
import com.livestock.backend.model.UserRole;
import com.livestock.backend.repository.AuthUserRepository;
import com.livestock.backend.repository.ProfileRepository;
import com.livestock.backend.repository.UserRoleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private RestTemplate restTemplate;

    @Value("${supabase.url}")
    private String supabaseUrl;

    @Value("${supabase.service.key}")
    private String serviceKey;

    @Autowired
    private AuthUserRepository authUserRepository;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Transactional(readOnly = true)
    public List<UserDTO> getAll() {
        logger.info("Fetching all users");
        List<AuthUser> users = authUserRepository.findAll();
        return users.stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public UserDTO getById(UUID id) {
        logger.info("Fetching user by ID: {}", id);
        AuthUser user = authUserRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        return toDTO(user);
    }

    @Transactional
    public UserDTO create(UserCreateDTO dto) {
        logger.info("Creating user: {}", dto.getEmail());
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + serviceKey);
        headers.set("Content-Type", "application/json");

        Map<String, Object> body = Map.of(
                "email", dto.getEmail(),
                "password", dto.getPassword(),
                "phone", dto.getPhone(),
                "confirm", true
        );

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
        ResponseEntity<Map> response = restTemplate.exchange(supabaseUrl + "/auth/v1/admin/users", HttpMethod.POST, entity, Map.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            Map userMap = response.getBody();
            UUID userId = UUID.fromString((String) userMap.get("id"));

            Profile profile = new Profile();
            profile.setId(userId);
            profile.setUsername(dto.getUsername());
            profile.setFullName(dto.getFullName());
            profile.setAvatarUrl(dto.getAvatarUrl());
            profile.setUpdatedAt(LocalDateTime.now());
            profileRepository.save(profile);

            UserRole role = new UserRole();
            role.setUserId(userId);
            role.setRole(dto.getRole());
            role.setCreatedAt(LocalDateTime.now());
            userRoleRepository.save(role);

            return getById(userId);
        } else {
            throw new RuntimeException("Failed to create user in Supabase: " + response.getBody());
        }
    }

    @Transactional
    public UserDTO update(UUID id, UserDTO dto) {
        logger.info("Updating user: {}", id);
        // Update auth.users via admin API if email/password changed
        if (dto.getEmail() != null || dto.getPhone() != null) {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + serviceKey);
            headers.set("Content-Type", "application/json");

            Map<String, Object> body = Map.of(
                    "email", dto.getEmail(),
                    "phone", dto.getPhone()
            );

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
            restTemplate.exchange(supabaseUrl + "/auth/v1/admin/users/" + id, HttpMethod.PUT, entity, Map.class);
        }

        // Update profile
        Profile profile = profileRepository.findById(id).orElseThrow(() -> new RuntimeException("Profile not found"));
        if (dto.getUsername() != null) profile.setUsername(dto.getUsername());
        if (dto.getFullName() != null) profile.setFullName(dto.getFullName());
        if (dto.getAvatarUrl() != null) profile.setAvatarUrl(dto.getAvatarUrl());
        profile.setUpdatedAt(LocalDateTime.now());
        profileRepository.save(profile);

        // Update role if provided
        if (dto.getRole() != null) {
            UserRole role = userRoleRepository.findByUserId(id).orElseThrow(() -> new RuntimeException("Role not found"));
            role.setRole(dto.getRole());
            userRoleRepository.save(role);
        }

        return getById(id);
    }

    @Transactional
    public void delete(UUID id) {
        logger.info("Deleting user: {}", id);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + serviceKey);
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        restTemplate.exchange(supabaseUrl + "/auth/v1/admin/users/" + id, HttpMethod.DELETE, entity, Void.class);
        // Profiles and roles cascade delete in DB
    }

    private UserDTO toDTO(AuthUser user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        Profile profile = profileRepository.findById(user.getId()).orElse(null);
        if (profile != null) {
            dto.setUsername(profile.getUsername());
            dto.setFullName(profile.getFullName());
            dto.setAvatarUrl(profile.getAvatarUrl());
            dto.setUpdatedAt(profile.getUpdatedAt());
        }
        UserRole role = userRoleRepository.findByUserId(user.getId()).orElse(null);
        if (role != null) {
            dto.setRole(role.getRole());
        }
        return dto;
    }
}