package com.livestock.backend.service;


import com.livestock.backend.dto.request.OwnerRequest;
import com.livestock.backend.dto.response.OwnerResponse;
import com.livestock.backend.exception.ResourceNotFoundException;
import com.livestock.backend.model.Owner;
import com.livestock.backend.repository.OwnerRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

@Service
public class OwnerService {

    private final OwnerRepository ownerRepository;
    private final OwnerMapper ownerMapper;

    public OwnerService(OwnerRepository ownerRepository, OwnerMapper ownerMapper) {
        this.ownerRepository = ownerRepository;
        this.ownerMapper = ownerMapper;
    }

    public Page<OwnerResponse> getAllOwners(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ownerRepository.findAll(pageable).map(ownerMapper::toResponse);
    }

    public OwnerResponse getOwnerById(UUID id) {
        Owner owner = ownerRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Owner not found"));
        OwnerResponse response = ownerMapper.toResponse(owner);
        response.setTotalAnimals(owner.getAnimals() != null ? owner.getAnimals().size() : 0);
        return response;
    }

    public OwnerResponse createOwner(OwnerRequest request) {
        Owner owner = ownerMapper.toEntity(request);
        owner.setCreatedAt(new Date());
        owner = ownerRepository.save(owner);
        return ownerMapper.toResponse(owner);
    }

    public OwnerResponse updateOwner(UUID id, OwnerRequest request) {
        Owner owner = ownerRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Owner not found"));
        owner.setName(request.getName());
        owner.setEmail(request.getEmail());
        owner.setPhone(request.getPhone());
        owner.setAddress(request.getAddress());
        owner.setNationalId(request.getNationalId());
        owner.setBankAccount(request.getBankAccount());
        owner.setEmergencyContact(request.getEmergencyContact());
        owner.setNotes(request.getNotes());
        owner = ownerRepository.save(owner);
        return ownerMapper.toResponse(owner);
    }

    public void deleteOwner(UUID id) {
        Owner owner = ownerRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Owner not found"));
        if (!owner.getAnimals().isEmpty()) {
            throw new RuntimeException("Cannot delete owner with associated animals");
        }
        ownerRepository.deleteById(id);
    }
}