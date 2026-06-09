package com.example.livestock.service;

import com.example.livestock.dto.OwnerRequest;
import com.example.livestock.dto.OwnerResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OwnerService {
    OwnerResponse create(OwnerRequest request);
    Page<OwnerResponse> findAll(Pageable pageable);
    OwnerResponse update(Long id, OwnerRequest request);
    void deactivate(Long id);
    OwnerResponse currentProfile();
}
