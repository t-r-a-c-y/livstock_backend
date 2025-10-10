package com.livestock.backend.service;

import com.livestock.backend.dto.OwnerDTO;
import com.livestock.backend.model.Owner;
import com.livestock.backend.repository.OwnerRepository;
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
public class OwnerService {
    private static final Logger logger = LoggerFactory.getLogger(OwnerService.class);

    @Autowired
    private OwnerRepository ownerRepository;

    @Transactional(readOnly = true)
    public Page<OwnerDTO> getAll(Pageable pageable) {
        logger.info("Fetching all owners");
        return ownerRepository.findAll(pageable).map(this::toDTO);
    }

    @Transactional(readOnly = true)
    public OwnerDTO getById(UUID id) {
        logger.info("Fetching owner with id: {}", id);
        Owner owner = ownerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Owner not found"));
        return toDTO(owner);
    }

    @Transactional
    public OwnerDTO create(OwnerDTO dto) {
        logger.info("Creating owner with name: {}", dto.getName());
        Owner owner = new Owner();
        updateEntityFromDTO(owner, dto);
        owner.setCreatedAt(LocalDateTime.now());
        owner.setUpdatedAt(LocalDateTime.now());
        owner = ownerRepository.save(owner);
        return toDTO(owner);
    }

    @Transactional
    public OwnerDTO update(UUID id, OwnerDTO dto) {
        logger.info("Updating owner with id: {}", id);
        Owner owner = ownerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Owner not found"));
        updateEntityFromDTO(owner, dto);
        owner.setUpdatedAt(LocalDateTime.now());
        owner = ownerRepository.save(owner);
        return toDTO(owner);
    }

    @Transactional
    public void delete(UUID id) {
        logger.info("Soft deleting owner with id: {}", id);
        Owner owner = ownerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Owner not found"));
        owner.setDeletedAt(LocalDateTime.now());
        ownerRepository.save(owner);
    }

    private OwnerDTO toDTO(Owner owner) {
        OwnerDTO dto = new OwnerDTO();
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
        dto.setDeletedAt(owner.getDeletedAt());
        return dto;
    }

    private void updateEntityFromDTO(Owner owner, OwnerDTO dto) {
        owner.setName(dto.getName());
        owner.setEmail(dto.getEmail());
        owner.setPhone(dto.getPhone());
        owner.setAddress(dto.getAddress());
        owner.setAvatar(dto.getAvatar());
        owner.setNationalId(dto.getNationalId());
        owner.setBankAccount(dto.getBankAccount());
        owner.setEmergencyContact(dto.getEmergencyContact());
        owner.setNotes(dto.getNotes());
    }
}