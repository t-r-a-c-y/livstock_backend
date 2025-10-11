package com.livestock.backend.service;

import com.livestock.backend.dto.OwnerDTO;
import com.livestock.backend.dto.OwnerCreateDTO;
import com.livestock.backend.dto.OwnerUpdateDTO;
import com.livestock.backend.model.Owner;
import com.livestock.backend.repository.OwnerRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OwnerService {
    private static final Logger logger = LoggerFactory.getLogger(OwnerService.class);
    private final OwnerRepository ownerRepository;
    private final ModelMapper modelMapper;

    public Page<OwnerDTO> getAllOwners(Pageable pageable) {
        logger.info("Fetching all owners");
        return ownerRepository.findAllActive(pageable)
                .map(owner -> modelMapper.map(owner, OwnerDTO.class));
    }

    public OwnerDTO getOwnerById(UUID id) {
        logger.info("Fetching owner with id: {}", id);
        Object[] result = ownerRepository.findByIdWithAnimalCount(id)
                .orElseThrow(() -> new RuntimeException("Owner not found or deleted"));
        Owner owner = (Owner) result[0];
        Long animalCount = (Long) result[1];
        OwnerDTO dto = modelMapper.map(owner, OwnerDTO.class);
        dto.setAnimalCount(animalCount);
        return dto;
    }

    @Transactional
    public OwnerDTO createOwner(OwnerCreateDTO dto) {
        logger.info("Creating new owner with email: {}", dto.getEmail());
        Owner owner = modelMapper.map(dto, Owner.class);
        owner.setCreatedAt(LocalDateTime.now());
        owner.setUpdatedAt(LocalDateTime.now());
        owner = ownerRepository.save(owner);
        return modelMapper.map(owner, OwnerDTO.class);
    }

    @Transactional
    public OwnerDTO updateOwner(UUID id, OwnerUpdateDTO dto) {
        logger.info("Updating owner with id: {}", id);
        Owner owner = ownerRepository.findById(id)
                .filter(o -> o.getDeletedAt() == null)
                .orElseThrow(() -> new RuntimeException("Owner not found or deleted"));
        modelMapper.map(dto, owner);
        owner.setUpdatedAt(LocalDateTime.now());
        owner = ownerRepository.save(owner);
        return modelMapper.map(owner, OwnerDTO.class);
    }

    @Transactional
    public void softDeleteOwner(UUID id) {
        logger.info("Soft deleting owner with id: {}", id);
        Owner owner = ownerRepository.findById(id)
                .filter(o -> o.getDeletedAt() == null)
                .orElseThrow(() -> new RuntimeException("Owner not found or already deleted"));
        owner.setDeletedAt(LocalDateTime.now());
        ownerRepository.save(owner);
    }
}