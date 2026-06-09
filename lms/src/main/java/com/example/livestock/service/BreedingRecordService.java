package com.example.livestock.service;

import com.example.livestock.dto.BreedingRecordRequest;
import com.example.livestock.dto.BreedingRecordResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BreedingRecordService {
    BreedingRecordResponse create(BreedingRecordRequest request);
    BreedingRecordResponse update(Long id, BreedingRecordRequest request);
    Page<BreedingRecordResponse> findAll(Pageable pageable);
    Page<BreedingRecordResponse> findMine(Pageable pageable);
}
