package com.livestock.service;

import com.livestock.dto.request.OwnerRequest;
import com.livestock.entity.Owner;
import com.livestock.repository.OwnerRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OwnerService {

    private final OwnerRepository ownerRepository;
    private final ModelMapper modelMapper;

    public Owner createOwner(OwnerRequest request) {
        Owner owner = modelMapper.map(request, Owner.class);
        return ownerRepository.save(owner);
    }

    public List<Owner> getAllOwners() {
        return ownerRepository.findAllByDeletedAtIsNull();
    }

    public Owner getOwnerById(UUID id) {
        return ownerRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new RuntimeException("Owner not found with id: " + id));
    }

    public Owner updateOwner(UUID id, OwnerRequest request) {
        Owner existing = getOwnerById(id);
        modelMapper.map(request, existing);
        existing.setUpdatedAt(LocalDateTime.now());
        return ownerRepository.save(existing);
    }

    public void deleteOwner(UUID id) {
        Owner owner = getOwnerById(id);
        owner.setDeletedAt(LocalDateTime.now());
        ownerRepository.save(owner);
    }
}