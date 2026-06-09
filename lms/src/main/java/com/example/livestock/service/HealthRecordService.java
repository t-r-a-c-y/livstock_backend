package com.example.livestock.service;

import com.example.livestock.dto.HealthRecordRequest;
import com.example.livestock.dto.HealthRecordResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface HealthRecordService {
    HealthRecordResponse create(HealthRecordRequest request);
    Page<HealthRecordResponse> findAll(Pageable pageable);
    Page<HealthRecordResponse> findMine(Pageable pageable);
}
