package com.livestock.service;

import com.livestock.dto.OwnerDto;
import com.livestock.entity.Owner;
import com.livestock.entity.User;
import com.livestock.entity.enums.OwnerStatus;
import com.livestock.entity.enums.Role;
import com.livestock.repository.OwnerRepository;
import com.livestock.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OwnerService {

    private final OwnerRepository ownerRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Creates a new owner and automatically creates a linked user account with VIEWER role.
     * The new user gets a random temporary password and must change it on first login.
     */
    @Transactional
    public OwnerDto createOwner(OwnerDto dto) {
        // 1. Validate uniqueness
        if (dto.getEmail() == null || dto.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        }
        if (ownerRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already in use by another owner");
        }
        if (dto.getPhone() == null || dto.getPhone().trim().isEmpty()) {
            throw new IllegalArgumentException("Phone is required");
        }
        if (ownerRepository.findByPhone(dto.getPhone()).isPresent()) {
            throw new IllegalArgumentException("Phone number already in use by another owner");
        }
        if (dto.getNationalId() != null && !dto.getNationalId().trim().isEmpty() &&
                ownerRepository.findByNationalId(dto.getNationalId()).isPresent()) {
            throw new IllegalArgumentException("National ID already registered");
        }
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Email is already used for a system user");
        }

        // 2. Create Owner entity (ignore id/timestamps from input)
        Owner owner = new Owner();
        owner.setName(dto.getName());
        owner.setEmail(dto.getEmail());
        owner.setPhone(dto.getPhone());
        owner.setAddress(dto.getAddress());
        owner.setAvatar(dto.getAvatar());
        owner.setNationalId(dto.getNationalId());
        owner.setBankAccount(dto.getBankAccount());
        owner.setEmergencyContact(dto.getEmergencyContact());
        owner.setNotes(dto.getNotes());
        owner.setStatus(OwnerStatus.PENDING);
        owner.setCreatedAt(LocalDateTime.now());
        owner.setUpdatedAt(LocalDateTime.now());

        // 3. Auto-create linked User (VIEWER role + must change password)
        String tempPassword = generateSecureRandomPassword(12);
        String encodedTempPassword = passwordEncoder.encode(tempPassword);

        User user = new User();
        user.setEmail(dto.getEmail());
        user.setPasswordHash(encodedTempPassword);
        user.setFirstName(extractFirstName(dto.getName()));
        user.setLastName(extractLastName(dto.getName()));
        user.setPhone(dto.getPhone());
        user.setRole(Role.VIEWER);
        user.setMustChangePassword(true);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        // Link them (adjust based on your entity relationship)
        owner.setLinkedUser(user);
        // user.setOwner(owner);   // ← uncomment if User has @ManyToOne or @OneToOne to Owner

        // 4. Save owner (user is saved via cascade if configured)
        Owner savedOwner = ownerRepository.save(owner);

        // 5. Development / testing output — REMOVE BEFORE PRODUCTION!
        System.out.println("=================================================================");
        System.out.println("DEV ONLY - New owner created: " + dto.getEmail());
        System.out.println("Temporary password (must be changed on first login): " + tempPassword);
        System.out.println("=================================================================");

        // TODO: In production, send this via email/SMS instead of console
        // emailService.sendOwnerWelcomeEmail(dto.getEmail(), dto.getName(), tempPassword);

        return mapToDto(savedOwner);
    }

    // ───────────────────────────────────────────────────────────────
    //                   Existing methods (lightly improved)
    // ───────────────────────────────────────────────────────────────

    @Transactional
    public OwnerDto updateOwner(UUID id, OwnerDto dto) {
        Owner existing = ownerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Owner not found"));

        // Prevent changing to already-used email/phone
        if (!existing.getEmail().equals(dto.getEmail()) &&
                ownerRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already in use");
        }
        if (!existing.getPhone().equals(dto.getPhone()) &&
                ownerRepository.findByPhone(dto.getPhone()).isPresent()) {
            throw new IllegalArgumentException("Phone already in use");
        }

        updateFields(existing, dto);
        existing.setUpdatedAt(LocalDateTime.now());

        Owner saved = ownerRepository.save(existing);
        return mapToDto(saved);
    }

    @Transactional(readOnly = true)
    public OwnerDto getOwnerById(UUID id) {
        return mapToDto(ownerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Owner not found")));
    }

    @Transactional(readOnly = true)
    public List<OwnerDto> getAllOwners() {
        return ownerRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteOwner(UUID id) {
        Owner owner = ownerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Owner not found"));

        // Optional: also delete linked user (if you want cascade delete)
        if (owner.getLinkedUser() != null) {
            userRepository.delete(owner.getLinkedUser());
        }

        ownerRepository.delete(owner);
    }

    @Transactional(readOnly = true)
    public OwnerDto getOwnerByEmail(String email) {
        return mapToDto(ownerRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Owner not found")));
    }

    @Transactional(readOnly = true)
    public List<OwnerDto> searchOwnersByName(String namePart) {
        return ownerRepository.findByNameContainingIgnoreCase(namePart).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    // ───────────────────────────────────────────────────────────────
    //                      Mapping & Helper methods
    // ───────────────────────────────────────────────────────────────

    private OwnerDto mapToDto(Owner owner) {
        OwnerDto dto = new OwnerDto();
        dto.setId(owner.getId());
        dto.setName(owner.getName());
        dto.setEmail(owner.getEmail());
        dto.setPhone(owner.getPhone());
        dto.setAddress(owner.getAddress());
        dto.setAvatar(owner.getAvatar());
        dto.setNationalId(owner.getNationalId());
        dto.setBankAccount(owner.getBankAccount());
        dto.setEmergencyContact(owner.getEmergencyContact());
        dto.setNotes(owner.getNotes());
        dto.setCreatedAt(owner.getCreatedAt());
        dto.setUpdatedAt(owner.getUpdatedAt());

        // Optional: expose status and linked user id
        if (owner.getStatus() != null) {
            dto.setStatus(owner.getStatus().name());  // add this field to OwnerDto if needed
        }
        if (owner.getLinkedUser() != null) {
            dto.setLinkedUserId(owner.getLinkedUser().getId());  // add this field to OwnerDto if needed
        }

        return dto;
    }

    private void updateFields(Owner target, OwnerDto source) {
        target.setName(source.getName());
        target.setEmail(source.getEmail());
        target.setPhone(source.getPhone());
        target.setAddress(source.getAddress());
        target.setAvatar(source.getAvatar());
        target.setNationalId(source.getNationalId());
        target.setBankAccount(source.getBankAccount());
        target.setEmergencyContact(source.getEmergencyContact());
        target.setNotes(source.getNotes());
        // Do NOT update status here — use a separate activation method
    }

    private String generateSecureRandomPassword(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()_+-=[]{}|";
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    private String extractFirstName(String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) return "Owner";
        String[] parts = fullName.trim().split("\\s+");
        return parts[0];
    }

    private String extractLastName(String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) return "";
        String[] parts = fullName.trim().split("\\s+");
        return parts.length > 1 ? parts[parts.length - 1] : "";
    }
}