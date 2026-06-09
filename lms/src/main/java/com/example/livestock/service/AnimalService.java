package com.example.livestock.service;

import com.example.livestock.dto.AnimalRequest;
import com.example.livestock.dto.AnimalResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AnimalService {
    AnimalResponse create(AnimalRequest request);
    Page<AnimalResponse> findAll(boolean includeInactive, Pageable pageable);
    Page<AnimalResponse> findMine(Pageable pageable);
    AnimalResponse update(Long id, AnimalRequest request);
    void deactivate(Long id);
}
