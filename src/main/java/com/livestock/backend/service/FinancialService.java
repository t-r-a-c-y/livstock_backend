package com.livestock.backend.service;

import com.livestock.backend.dto.request.FinancialRequest;
import com.livestock.backend.dto.response.FinancialResponse;
import com.livestock.backend.exception.ResourceNotFoundException;
import com.livestock.backend.model.FinancialRecord;
import com.livestock.backend.model.UserProfile;
import com.livestock.backend.repository.FinancialRecordRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class FinancialService {

    private final FinancialRecordRepository financialRecordRepository;
    private final FinancialMapper financialMapper;
    private final AuthService authService;

    public FinancialService(FinancialRecordRepository financialRecordRepository, FinancialMapper financialMapper, AuthService authService) {
        this.financialRecordRepository = financialRecordRepository;
        this.financialMapper = financialMapper;
        this.authService = authService;
    }

    public FinancialResponse createFinancialRecord(FinancialRequest request) {
        UserProfile currentUser = authService.getCurrentUser();
        FinancialRecord record = financialMapper.toEntity(request);
        record.setOwner(currentUser.getOwner());
        record = financialRecordRepository.save(record);
        return financialMapper.toResponse(record);
    }

    public FinancialResponse getFinancialRecordById(UUID id) {
        FinancialRecord record = financialRecordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Financial record not found with id: " + id));
        return financialMapper.toResponse(record);
    }

    public Page<FinancialResponse> getAllFinancialRecords(Pageable pageable) {
        UserProfile currentUser = authService.getCurrentUser();
        Page<FinancialRecord> records = financialRecordRepository.findByOwnerId(currentUser.getOwner().getId(), pageable);
        return records.map(financialMapper::toResponse);
    }

    public List<FinancialResponse> getAllFinancialRecords() {
        UserProfile currentUser = authService.getCurrentUser();
        List<FinancialRecord> records = financialRecordRepository.findByOwnerId(currentUser.getOwner().getId());
        return records.stream()
                .map(financialMapper::toResponse)
                .toList();
    }

    public FinancialResponse updateFinancialRecord(UUID id, FinancialRequest request) {
        FinancialRecord record = financialRecordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Financial record not found with id: " + id));
        UserProfile currentUser = authService.getCurrentUser();
        if (!record.getOwner().getId().equals(currentUser.getOwner().getId())) {
            throw new SecurityException("Unauthorized to update this financial record");
        }
        FinancialRecord updatedRecord = financialMapper.toEntity(request);
        updatedRecord.setId(id);
        updatedRecord.setOwner(record.getOwner());
        updatedRecord = financialRecordRepository.save(updatedRecord);
        return financialMapper.toResponse(updatedRecord);
    }

    public void deleteFinancialRecord(UUID id) {
        FinancialRecord record = financialRecordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Financial record not found with id: " + id));
        UserProfile currentUser = authService.getCurrentUser();
        if (!record.getOwner().getId().equals(currentUser.getOwner().getId())) {
            throw new SecurityException("Unauthorized to delete this financial record");
        }
        financialRecordRepository.delete(record);
    }
}