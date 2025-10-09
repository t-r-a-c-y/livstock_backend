package com.livestock.backend.service;

import com.livestock.backend.dto.OwnerDTO;
import com.livestock.backend.dto.OwnerWithAnimalCountDTO;
import com.livestock.backend.model.Owner;
import com.livestock.backend.repository.AnimalRepository;
import com.livestock.backend.repository.OwnerRepository;
import org.slf4j.Logger;
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
    public Page<OwnerDTO> getAll(Pageable pageable) {
        logger.info("Fetching owners");
        Specification<Owner> spec = Specification.where((root, query, cb) -> cb.isNull(root.get("deletedAt")));
        return ownerRepository.findAll(spec, pageable).map(this::toDTO);
    }

    @Transactional(readOnly = true)
    public OwnerWithAnimalCountDTO getById(UUID id) {
        logger.info("Fetching owner by ID: {}", id);
        Owner owner = ownerRepository.findById(id).orElseThrow(() -> new RuntimeException("Owner not found"));
        if (owner.getDeletedAt() != null) throw new RuntimeException("Owner deleted");
        long animalCount = animalRepository.countByOwnerIdAndDeletedAtIsNull(id);
        OwnerWithAnimalCountDTO dto = new OwnerWithAnimalCountDTO();
        dto.setOwner(toDTO(owner));
        dto.setAnimalCount(animalCount);
        return dto;
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