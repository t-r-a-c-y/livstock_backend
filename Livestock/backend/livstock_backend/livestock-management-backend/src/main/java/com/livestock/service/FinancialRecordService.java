package com.livestock.service;

import com.livestock.dto.request.FinancialRecordRequest;
import com.livestock.dto.response.FinancialRecordResponse;
import com.livestock.entity.FinancialRecord;
import com.livestock.entity.Owner;
import com.livestock.repository.FinancialRecordRepository;
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
public class FinancialRecordService {

    private final FinancialRecordRepository financialRecordRepository;
    private final OwnerRepository ownerRepository;
    private final ModelMapper modelMapper;

    public FinancialRecordResponse createFinancialRecord(FinancialRecordRequest request, UUID ownerId) {
        Owner owner = null;
        if (ownerId != null) {
            owner = ownerRepository.findByIdAndDeletedAtIsNull(ownerId)
                    .orElseThrow(() -> new RuntimeException("Owner not found with id: " + ownerId));
        }

        FinancialRecord record = modelMapper.map(request, FinancialRecord.class);
        if (owner != null) {
            record.setOwner(owner);
            record.setOwnerName(owner.getName());
        }

        FinancialRecord saved = financialRecordRepository.save(record);
        return modelMapper.map(saved, FinancialRecordResponse.class);
    }

    public List<FinancialRecordResponse> getAllFinancialRecords() {
        List<FinancialRecord> records = financialRecordRepository.findAllByDeletedAtIsNull();
        return records.stream()
                .map(r -> modelMapper.map(r, FinancialRecordResponse.class))
                .collect(Collectors.toList());
    }

    public FinancialRecordResponse getFinancialRecordById(UUID id) {
        FinancialRecord record = financialRecordRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new RuntimeException("Financial record not found with id: " + id));
        return modelMapper.map(record, FinancialRecordResponse.class);
    }

    public FinancialRecordResponse updateFinancialRecord(UUID id, FinancialRecordRequest request) {
        FinancialRecord existing = financialRecordRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new RuntimeException("Financial record not found with id: " + id));

        modelMapper.map(request, existing);
        existing.setUpdatedAt(LocalDateTime.now());

        FinancialRecord updated = financialRecordRepository.save(existing);
        return modelMapper.map(updated, FinancialRecordResponse.class);
    }

    public void deleteFinancialRecord(UUID id) {
        FinancialRecord record = financialRecordRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new RuntimeException("Financial record not found with id: " + id));
        record.setDeletedAt(LocalDateTime.now());
        financialRecordRepository.save(record);
    }
}