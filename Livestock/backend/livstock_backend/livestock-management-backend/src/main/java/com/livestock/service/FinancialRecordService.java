// src/main/java/com/livestock/service/FinancialRecordService.java
package com.livestock.service;

import com.livestock.dto.request.FinancialRecordRequest;
import com.livestock.dto.response.FinancialRecordResponse;
import com.livestock.entity.Animal;
import com.livestock.entity.FinancialRecord;
import com.livestock.entity.Owner;
import com.livestock.exception.ResourceNotFoundException;
import com.livestock.repository.AnimalRepository;
import com.livestock.repository.FinancialRecordRepository;
import com.livestock.repository.OwnerRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FinancialRecordService {

    private final FinancialRecordRepository financialRecordRepository;
    private final OwnerRepository ownerRepository;
    private final AnimalRepository animalRepository;
    private final ModelMapper modelMapper;

    public List<FinancialRecordResponse> getAllFinancialRecords(
            String type, String category, LocalDateTime from, LocalDateTime to,
            UUID ownerId, UUID animalId) {

        List<FinancialRecord> records;

        if (from != null && to != null) {
            records = financialRecordRepository.findByDateRange(from, to);
        } else if (ownerId != null) {
            records = financialRecordRepository.findByOwnerIdAndDeletedAtIsNull(ownerId);
        } else if (animalId != null) {
            records = financialRecordRepository.findByAnimalIdAndDeletedAtIsNull(animalId);
        } else if (type != null && category != null) {
            records = financialRecordRepository.findByCategory(category);
        } else if (type != null) {
            records = financialRecordRepository.findByTypeAndDeletedAtIsNull(type);
        } else {
            records = financialRecordRepository.findAllActive();
        }

        return records.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public FinancialRecordResponse getFinancialRecordById(UUID id) {
        FinancialRecord record = financialRecordRepository.findActiveById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Financial record not found"));
        return mapToResponse(record);
    }

    public FinancialRecordResponse createFinancialRecord(FinancialRecordRequest request) {
        Owner owner = request.getOwnerId() != null ?
                ownerRepository.findActiveById(request.getOwnerId()).orElse(null) : null;
        Animal animal = request.getAnimalId() != null ?
                animalRepository.findActiveById(request.getAnimalId()).orElse(null) : null;

        FinancialRecord record = modelMapper.map(request, FinancialRecord.class);
        record.setOwner(owner);
        record.setAnimal(animal);
        record = financialRecordRepository.save(record);

        return mapToResponse(record);
    }

    public FinancialRecordResponse updateFinancialRecord(UUID id, FinancialRecordRequest request) {
        FinancialRecord record = financialRecordRepository.findActiveById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Financial record not found"));

        Owner owner = request.getOwnerId() != null ?
                ownerRepository.findActiveById(request.getOwnerId()).orElse(null) : null;
        Animal animal = request.getAnimalId() != null ?
                animalRepository.findActiveById(request.getAnimalId()).orElse(null) : null;

        modelMapper.map(request, record);
        record.setOwner(owner);
        record.setAnimal(animal);
        record.setUpdatedAt(LocalDateTime.now());
        record = financialRecordRepository.save(record);

        return mapToResponse(record);
    }

    public void deleteFinancialRecord(UUID id) {
        FinancialRecord record = financialRecordRepository.findActiveById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Financial record not found"));
        record.setDeletedAt(LocalDateTime.now());
        financialRecordRepository.save(record);
    }

    // Dashboard summary
    public record FinancialSummary(
            BigDecimal totalIncome,
            BigDecimal totalExpense,
            BigDecimal profit
    ) {}

    public FinancialSummary getSummary() {
        List<FinancialRecord> all = financialRecordRepository.findAllActive();

        BigDecimal income = all.stream()
                .filter(r -> "income".equalsIgnoreCase(r.getType()))
                .map(FinancialRecord::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal expense = all.stream()
                .filter(r -> "expense".equalsIgnoreCase(r.getType()))
                .map(FinancialRecord::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new FinancialSummary(income, expense, income.subtract(expense));
    }

    private FinancialRecordResponse mapToResponse(FinancialRecord record) {
        FinancialRecordResponse response = modelMapper.map(record, FinancialRecordResponse.class);
        if (record.getOwner() != null) {
            response.setOwnerId(record.getOwner().getId());
            response.setOwnerName(record.getOwner().getName());
        }
        if (record.getAnimal() != null) {
            response.setAnimalId(record.getAnimal().getId());
            response.setAnimalTagId(record.getAnimal().getTagId());
        }
        return response;
    }
}