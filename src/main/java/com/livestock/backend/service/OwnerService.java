package com.livestock.backend.service;

import com.livestock.backend.dto.OwnerDTO;
import com.livestock.backend.dto.OwnerWithAnimalCountDTO;
import com.livestock.backend.model.Owner;
import com.livestock.backend.repository.AnimalRepository;
import com.livestock.backend.repository.OwnerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class OwnerService {
    private static final Logger logger = LoggerFactory.getLogger(OwnerService.class);

    @Autowired
    private OwnerRepository ownerRepository;

    @Autowired
    private AnimalRepository animalRepository;

    @Transactional(readOnly = true)
    public Page<OwnerWithAnimalCountDTO> getAll(String name, Pageable pageable) {
        logger.info("Fetching owners with name filter: {}", name);
        Specification<Owner> spec = (root, query, cb) -> cb.isNull(root.get("deletedAt"));
        if (name != null) {
            spec = spec.and((root, query, cb) -> cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
        }
        return ownerRepository.findAll(spec, pageable).map(this::toDTOWithCount);
    }

    @Transactional(readOnly = true)
    public OwnerDTO getById(UUID id) {
        logger.info("Fetching owner by ID: {}", id);
        Owner owner = ownerRepository.findById(id).orElseThrow(() -> new RuntimeException("Owner not found"));
        if (owner.getDeletedAt() != null) throw new RuntimeException("Owner deleted");
        return toDTO(owner);
    }

    @Transactional
    public OwnerDTO create(OwnerDTO dto) {
        logger.info("Creating owner: {}", dto.getName());
        Owner owner = new Owner();
        mapToEntity(dto, owner);
        owner.setCreatedAt(LocalDateTime.now());
        owner.setUpdatedAt(LocalDateTime.now());
        owner = ownerRepository.save(owner);
        return toDTO(owner);
    }

    @Transactional
    public OwnerDTO update(UUID id, OwnerDTO dto) {
        logger.info("Updating owner: {}", id);
        Owner owner = ownerRepository.findById(id).orElseThrow(() -> new RuntimeException("Owner not found"));
        if (owner.getDeletedAt() != null) throw new RuntimeException("Owner deleted");
        mapToEntity(dto, owner);
        owner.setUpdatedAt(LocalDateTime.now());
        owner = ownerRepository.save(owner);
        return toDTO(owner);
    }

    @Transactional
    public void softDelete(UUID id) {
        logger.info("Soft deleting owner: {}", id);
        Owner owner = ownerRepository.findById(id).orElseThrow(() -> new RuntimeException("Owner not found"));
        if (animalRepository.countByOwnerIdAndDeletedAtIsNull(id) > 0) {
            throw new RuntimeException("Cannot delete owner with active animals");
        }
        owner.setDeletedAt(LocalDateTime.now());
        ownerRepository.save(owner);
    }

    private OwnerDTO toDTO(Owner owner) {
        OwnerDTO dto = new OwnerDTO();
        dto.setId(owner.getId());
        dto.setName(owner.getName());
        dto.setPhone(owner.getPhone());
        dto.setEmail(owner.getEmail());
        dto.setAddress(owner.getAddress());
        dto.setNationalId(owner.getNationalId());
        dto.setBankAccount(owner.getBankAccount());
        dto.setEmergencyContact(owner.getEmergencyContact());
        dto.setCreatedAt(owner.getCreatedAt());
        dto.setUpdatedAt(owner.getUpdatedAt());
        return dto;
    }

    private OwnerWithAnimalCountDTO toDTOWithCount(Owner owner) {
        OwnerWithAnimalCountDTO dto = new OwnerWithAnimalCountDTO();
        dto.setOwner(toDTO(owner));
        dto.setAnimalCount(animalRepository.countByOwnerIdAndDeletedAtIsNull(owner.getId()));
        return dto;
    }

    private void mapToEntity(OwnerDTO dto, Owner owner) {
        owner.setName(dto.getName());
        owner.setPhone(dto.getPhone());
        owner.setEmail(dto.getEmail());
        owner.setAddress(dto.getAddress());
        owner.setNationalId(dto.getNationalId());
        owner.setBankAccount(dto.getBankAccount());
        owner.setEmergencyContact(dto.getEmergencyContact());
    }
}