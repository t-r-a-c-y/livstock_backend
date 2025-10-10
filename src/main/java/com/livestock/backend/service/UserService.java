package com.livestock.backend.service;

import com.livestock.backend.dto.ProfileDTO;
import com.livestock.backend.dto.UserRoleDTO;
import com.livestock.backend.model.Profile;
import com.livestock.backend.model.UserRole;
import com.livestock.backend.repository.ProfileRepository;
import com.livestock.backend.repository.UserRoleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Transactional(readOnly = true)
    public Page<ProfileDTO> getAll(Pageable pageable) {
        logger.info("Fetching all user profiles");
        return profileRepository.findAll(pageable).map(this::toProfileDTO);
    }

    @Transactional(readOnly = true)
    public ProfileDTO getById(UUID id) {
        logger.info("Fetching user profile with id: {}", id);
        Profile profile = profileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Profile not found"));
        return toProfileDTO(profile);
    }

    @Transactional
    public ProfileDTO create(ProfileDTO dto) {
        logger.info("Creating user profile with id: {}", dto.getId());
        Profile profile = new Profile();
        updateProfileFromDTO(profile, dto);
        profile.setCreatedAt(LocalDateTime.now());
        profile.setUpdatedAt(LocalDateTime.now());
        profile = profileRepository.save(profile);
        return toProfileDTO(profile);
    }

    @Transactional
    public ProfileDTO update(UUID id, ProfileDTO dto) {
        logger.info("Updating user profile with id: {}", id);
        Profile profile = profileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Profile not found"));
        updateProfileFromDTO(profile, dto);
        profile.setUpdatedAt(LocalDateTime.now());
        profile = profileRepository.save(profile);
        return toProfileDTO(profile);
    }

    @Transactional
    public void delete(UUID id) {
        logger.info("Deleting user profile with id: {}", id);
        Profile profile = profileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Profile not found"));
        profileRepository.delete(profile);
    }

    @Transactional
    public UserRoleDTO createRole(UserRoleDTO dto) {
        logger.info("Creating user role for userId: {}", dto.getUserId());
        UserRole role = new UserRole();
        updateRoleFromDTO(role, dto);
        role.setCreatedAt(LocalDateTime.now());
        role = userRoleRepository.save(role);
        return toRoleDTO(role);
    }

    private ProfileDTO toProfileDTO(Profile profile) {
        ProfileDTO dto = new ProfileDTO();
        dto.setId(profile.getId());
        dto.setName(profile.getName());
        dto.setPhone(profile.getPhone());
        dto.setAvatar(profile.getAvatar());
        dto.setCreatedAt(profile.getCreatedAt());
        dto.setUpdatedAt(profile.getUpdatedAt());
        return dto;
    }

    private void updateProfileFromDTO(Profile profile, ProfileDTO dto) {
        profile.setId(dto.getId());
        profile.setName(dto.getName());
        profile.setPhone(dto.getPhone());
        profile.setAvatar(dto.getAvatar());
    }

    private UserRoleDTO toRoleDTO(UserRole role) {
        UserRoleDTO dto = new UserRoleDTO();
        dto.setId(role.getId());
        dto.setUserId(role.getUserId());
        dto.setRole(role.getRole());
        dto.setCreatedAt(role.getCreatedAt());
        return dto;
    }

    private void updateRoleFromDTO(UserRole role, UserRoleDTO dto) {
        role.setUserId(dto.getUserId());
        role.setRole(dto.getRole());
    }
}