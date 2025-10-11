package com.livestock.backend.service;

import com.livestock.backend.dto.UserDTO;
import com.livestock.backend.dto.UserCreateDTO;
import com.livestock.backend.model.Profile;
import com.livestock.backend.model.UserRole;
import com.livestock.backend.repository.ProfileRepository;
import com.livestock.backend.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final ProfileRepository profileRepository;
    private final UserRoleRepository userRoleRepository;
    private final ModelMapper modelMapper;
    private final RestTemplate restTemplate; // For Supabase auth calls
    private final String supabaseUrl = "https://<your-supabase>.supabase.co/auth/v1/signup";
    private final String supabaseApiKey = "<your-supabase-api-key>";

    public List<UserDTO> getAllUsers() {
        logger.info("Fetching all users");
        List<Profile> profiles = profileRepository.findAllActive();
        return profiles.stream().map(profile -> {
            UserDTO dto = modelMapper.map(profile, UserDTO.class);
            List<String> roles = userRoleRepository.findByUserId(profile.getId())
                    .stream().map(UserRole::getRole).collect(Collectors.toList());
            dto.setRoles(roles);
            return dto;
        }).collect(Collectors.toList());
    }

    public UserDTO getUserById(UUID id) {
        logger.info("Fetching user with id: {}", id);
        Profile profile = profileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User profile not found"));
        UserDTO dto = modelMapper.map(profile, UserDTO.class);
        List<String> roles = userRoleRepository.findByUserId(id)
                .stream().map(UserRole::getRole).collect(Collectors.toList());
        dto.setRoles(roles);
        return dto;
    }

    @Transactional
    public UserDTO createUser(UserCreateDTO dto) {
        logger.info("Creating new user with email: {}", dto.getEmail());
        // Call Supabase auth API
        String supabaseResponse = restTemplate.postForObject(
                supabaseUrl,
                new SupabaseAuthRequest(dto.getEmail(), dto.getPassword()),
                String.class
        );
        // Parse response to get user ID (assuming JSON response with "id")
        UUID userId = parseSupabaseUserId(supabaseResponse);

        // Create profile
        Profile profile = new Profile();
        profile.setId(userId);
        profile.setName(dto.getName());
        profile.setPhone(dto.getPhone());
        profile.setAvatar(dto.getAvatar());
        profile.setCreatedAt(LocalDateTime.now());
        profile.setUpdatedAt(LocalDateTime.now());
        profile = profileRepository.save(profile);

        // Create roles
        if (dto.getRoles() != null) {
            for (String role : dto.getRoles()) {
                UserRole userRole = new UserRole();
                userRole.setUserId(userId);
                userRole.setRole(role);
                userRole.setCreatedAt(LocalDateTime.now());
                userRoleRepository.save(userRole);
            }
        }

        UserDTO result = modelMapper.map(profile, UserDTO.class);
        result.setRoles(dto.getRoles());
        return result;
    }

    @Transactional
    public UserDTO updateUser(UUID id, UserDTO dto) {
        logger.info("Updating user with id: {}", id);
        Profile profile = profileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User profile not found"));
        modelMapper.map(dto, profile);
        profile.setUpdatedAt(LocalDateTime.now());
        profile = profileRepository.save(profile);

        // Update roles
        userRoleRepository.findByUserId(id).forEach(userRoleRepository::delete);
        if (dto.getRoles() != null) {
            for (String role : dto.getRoles()) {
                UserRole userRole = new UserRole();
                userRole.setUserId(id);
                userRole.setRole(role);
                userRole.setCreatedAt(LocalDateTime.now());
                userRoleRepository.save(userRole);
            }
        }

        UserDTO result = modelMapper.map(profile, UserDTO.class);
        result.setRoles(dto.getRoles());
        return result;
    }

    @Transactional
    public void deleteUser(UUID id) {
        logger.info("Deleting user with id: {}", id);
        profileRepository.deleteById(id);
        userRoleRepository.findByUserId(id).forEach(userRoleRepository::delete);
        // Optionally call Supabase API to delete user
    }

    private UUID parseSupabaseUserId(String response) {
        // Implement JSON parsing to extract user ID
        return UUID.randomUUID(); // Replace with actual parsing
    }

    // Class for Supabase auth request
    private static class SupabaseAuthRequest {
        private String email;
        private String password;

        public SupabaseAuthRequest(String email, String password) {
            this.email = email;
            this.password = password;
        }

        // Getters and setters
        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
}