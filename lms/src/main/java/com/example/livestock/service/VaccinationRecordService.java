package com.example.livestock.service;

import com.example.livestock.dto.VaccinationRecordRequest;
import com.example.livestock.dto.VaccinationRecordResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface VaccinationRecordService {
    VaccinationRecordResponse create(VaccinationRecordRequest request);
    Page<VaccinationRecordResponse> findAll(Pageable pageable);
    Page<VaccinationRecordResponse> findMine(Pageable pageable);
    void createDueSoonNotifications();
}
