package com.livestock.service;

import com.livestock.dto.OwnerDto;
import com.livestock.entity.Owner;
import com.livestock.repository.OwnerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OwnerService {

    private final OwnerRepository ownerRepository;

    @Transactional
    public OwnerDto createOwner(OwnerDto dto) {
        if (ownerRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already in use");
        }
        if (ownerRepository.findByPhone(dto.getPhone()).isPresent()) {
            throw new IllegalArgumentException("Phone number already in use");
        }

        Owner owner = mapToEntity(dto);
        Owner saved = ownerRepository.save(owner);
        return mapToDto(saved);
    }

    @Transactional
    public OwnerDto updateOwner(UUID id, OwnerDto dto) {
        Owner existing = ownerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Owner not found"));

        // Allow email/phone change only if not taken by someone else
        if (!existing.getEmail().equals(dto.getEmail()) &&
                ownerRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already in use");
        }
        if (!existing.getPhone().equals(dto.getPhone()) &&
                ownerRepository.findByPhone(dto.getPhone()).isPresent()) {
            throw new IllegalArgumentException("Phone number already in use");
        }

        updateFields(existing, dto);
        Owner saved = ownerRepository.save(existing);
        return mapToDto(saved);
    }

    @Transactional(readOnly = true)
    public OwnerDto getOwnerById(UUID id) {
        return mapToDto(ownerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Owner not found")));
    }

    @Transactional(readOnly = true)
    public List<OwnerDto> getAllOwners() {
        return ownerRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteOwner(UUID id) {
        if (!ownerRepository.existsById(id)) {
            throw new RuntimeException("Owner not found");
        }
        ownerRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public OwnerDto getOwnerByEmail(String email) {
        return mapToDto(ownerRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Owner not found")));
    }

    @Transactional(readOnly = true)
    public List<OwnerDto> searchOwnersByName(String namePart) {
        return ownerRepository.findByNameContainingIgnoreCase(namePart).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    // Simple mapping
    private Owner mapToEntity(OwnerDto dto) {
        Owner owner = new Owner();
        owner.setId(dto.getId());
        owner.setName(dto.getName());
        owner.setEmail(dto.getEmail());
        owner.setPhone(dto.getPhone());
        owner.setAddress(dto.getAddress());
        owner.setAvatar(dto.getAvatar());
        owner.setNationalId(dto.getNationalId());
        owner.setBankAccount(dto.getBankAccount());
        owner.setEmergencyContact(dto.getEmergencyContact());
        owner.setNotes(dto.getNotes());
        return owner;
    }

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
    }
}