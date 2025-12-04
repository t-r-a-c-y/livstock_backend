// src/main/java/com/livestock/service/OwnerService.java
package com.livestock.service;

import com.livestock.dto.request.OwnerRequest;
import com.livestock.dto.response.OwnerResponse;
import com.livestock.entity.Owner;
import com.livestock.exception.ResourceNotFoundException;
import com.livestock.repository.OwnerRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OwnerService {

    private final OwnerRepository ownerRepository;
    private final ModelMapper modelMapper;

    public List<OwnerResponse> getAllOwners() {
        return ownerRepository.findAllActive().stream()
                .map(o -> modelMapper.map(o, OwnerResponse.class))
                .collect(Collectors.toList());
    }

    public OwnerResponse getOwnerById(UUID id) {
        Owner owner = ownerRepository.findActiveById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Owner not found"));
        return modelMapper.map(owner, OwnerResponse.class);
    }

    public OwnerResponse createOwner(OwnerRequest request) {
        Owner owner = modelMapper.map(request, Owner.class);
        owner = ownerRepository.save(owner);
        return modelMapper.map(owner, OwnerResponse.class);
    }

    public OwnerResponse updateOwner(UUID id, OwnerRequest request) {
        Owner owner = ownerRepository.findActiveById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Owner not found"));
        modelMapper.map(request, owner);
        owner.setUpdatedAt(LocalDateTime.now());
        owner = ownerRepository.save(owner);
        return modelMapper.map(owner, OwnerResponse.class);
    }

    public void deleteOwner(UUID id) {
        Owner owner = ownerRepository.findActiveById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Owner not found"));
        owner.setDeletedAt(LocalDateTime.now());
        ownerRepository.save(owner);
    }
}