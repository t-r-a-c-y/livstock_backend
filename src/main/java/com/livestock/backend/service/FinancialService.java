package com.livestock.backend.service;


import com.livestock.backend.dto.request.FinancialRequest;
import com.livestock.backend.dto.response.FinancialResponse;
import com.livestock.backend.exception.ResourceNotFoundException;
import com.livestock.backend.model.FinancialRecord;
import com.livestock.backend.repository.FinancialRepository;
import com.livestock.backend.repository.AnimalRepository;
import com.livestock.backend.repository.OwnerRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class FinancialService {

    private final FinancialRepository financialRepository;
    private final OwnerRepository ownerRepository;
    private final AnimalRepository animalRepository;
    private final FinancialMapper financialMapper;

    public FinancialService(FinancialRepository financialRepository, OwnerRepository ownerRepository, AnimalRepository animalRepository, FinancialMapper financialMapper) {
        this.financialRepository = financialRepository;
        this.ownerRepository = ownerRepository;
        this.animalRepository = animalRepository;
        this.financialMapper = financialMapper;
    }

    public Page<FinancialResponse> getAllFinancialRecords(int page, int size, String type, String category, Date dateFrom, Date dateTo, UUID ownerId) {
        Pageable pageable = PageRequest.of(page, size);
        Specification<FinancialRecord> spec = Specification.where(null);
        if (type != null) spec = spec.and((root, query, cb) -> cb.equal(root.get("type"), type));
        if (category != null) spec = spec.and((root, query, cb) -> cb.equal(root.get("category"), category));
        if (dateFrom != null) spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("date"), dateFrom));
        if (dateTo != null) spec = spec.and((root, query, cb) -> cb.lessThanOrEqualTo(root.get("date"), dateTo));
        if (ownerId != null) spec = spec.and((root, query, cb) -> cb.equal(root.get("owner").get("id"), ownerId));
        return financialRepository.findAll(spec, pageable).map(financialMapper::toResponse);
    }

    public FinancialResponse getFinancialRecordById(UUID id) {
        FinancialRecord record = financialRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Financial record not found"));
        return financialMapper.toResponse(record);
    }

    public FinancialResponse createFinancialRecord(FinancialRequest request) {
        FinancialRecord record = financialMapper.toEntity(request);
        if (request.getOwnerId() != null) {
            record.setOwner(ownerRepository.findById(request.getOwnerId()).orElseThrow(() -> new ResourceNotFoundException("Owner not found")));
        }
        if (request.getAnimalId() != null) {
            record.setAnimal(animalRepository.findById(request.getAnimalId()).orElseThrow(() -> new ResourceNotFoundException("Animal not found")));
        }
        record.setCreatedAt(new Date());
        record = financialRepository.save(record);
        return financialMapper.toResponse(record);
    }

    public FinancialResponse updateFinancialRecord(UUID id, FinancialRequest request) {
        FinancialRecord record = financialRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Financial record not found"));
        record.setType(request.getType());
        record.setCategory(request.getCategory());
        record.setAmount(request.getAmount());
        record.setDate(request.getDate());
        record.setDescription(request.getDescription());
        record.setPaymentMethod(request.getPaymentMethod());
        record.setReceiptNumber(request.getReceiptNumber());
        if (request.getOwnerId() != null) {
            record.setOwner(ownerRepository.findById(request.getOwnerId()).orElseThrow(() -> new ResourceNotFoundException("Owner not found")));
        }
        if (request.getAnimalId() != null) {
            record.setAnimal(animalRepository.findById(request.getAnimalId()).orElseThrow(() -> new ResourceNotFoundException("Animal not found")));
        }
        record = financialRepository.save(record);
        return financialMapper.toResponse(record);
    }

    public void deleteFinancialRecord(UUID id) {
        financialRepository.deleteById(id);
    }

    public Map<String, Object> getFinancialStats(Date dateFrom, Date dateTo) {
        Specification<FinancialRecord> spec = Specification.where(null);
        if (dateFrom != null) spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("date"), dateFrom));
        if (dateTo != null) spec = spec.and((root, query, cb) -> cb.lessThanOrEqualTo(root.get("date"), dateTo));
        List<FinancialRecord> records = financialRepository.findAll(spec);
        double totalIncome = records.stream().filter(r -> "Income".equals(r.getType())).mapToDouble(FinancialRecord::getAmount).sum();
        double totalExpense = records.stream().filter(r -> "Expense".equals(r.getType())).mapToDouble(FinancialRecord::getAmount).sum();
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalIncome", totalIncome);
        stats.put("totalExpense", totalExpense);
        stats.put("netProfit", totalIncome - totalExpense);
        // Add by category using stream groupBy
        return stats;
    }
}