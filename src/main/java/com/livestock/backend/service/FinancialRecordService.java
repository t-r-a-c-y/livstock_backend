package com.livestock.backend.service;

import com.livestock.backend.dto.FinancialRecordDTO;
import com.livestock.backend.dto.FinancialSummaryDTO;
import com.livestock.backend.model.FinancialRecord;
import com.livestock.backend.repository.FinancialRecordRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class FinancialRecordService {
    private static final Logger logger = LoggerFactory.getLogger(FinancialRecordService.class);

    @Autowired
    private FinancialRecordRepository financialRecordRepository;

    @Transactional(readOnly = true)
    public Page<FinancialRecordDTO> getAll(String type, LocalDate dateFrom, LocalDate dateTo, UUID animalId, Pageable pageable) {
        logger.info("Fetching financial records with type: {}, dateFrom: {}, dateTo: {}, animalId: {}", type, dateFrom, dateTo, animalId);
        Specification<FinancialRecord> spec = (root, query, cb) -> cb.isNull(root.get("deletedAt"));
        if (type != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("type"), type));
        }
        if (dateFrom != null) {
            spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("date"), dateFrom));
        }
        if (dateTo != null) {
            spec = spec.and((root, query, cb) -> cb.lessThanOrEqualTo(root.get("date"), dateTo));
        }
        if (animalId != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("animalId"), animalId));
        }
        return financialRecordRepository.findAll(spec, pageable).map(this::toDTO);
    }

    @Transactional(readOnly = true)
    public FinancialRecordDTO getById(UUID id) {
        logger.info("Fetching financial record with id: {}", id);
        FinancialRecord record = financialRecordRepository.findById(id)
                .filter(r -> r.getDeletedAt() == null)
                .orElseThrow(() -> new RuntimeException("Financial record not found"));
        return toDTO(record);
    }

    @Transactional
    public FinancialRecordDTO create(FinancialRecordDTO dto) {
        logger.info("Creating financial record with type: {}", dto.getType());
        FinancialRecord record = new FinancialRecord();
        updateEntityFromDTO(record, dto);
        record.setCreatedAt(LocalDateTime.now());
        record.setUpdatedAt(LocalDateTime.now());
        record = financialRecordRepository.save(record);
        return toDTO(record);
    }

    @Transactional
    public FinancialRecordDTO update(UUID id, FinancialRecordDTO dto) {
        logger.info("Updating financial record with id: {}", id);
        FinancialRecord record = financialRecordRepository.findById(id)
                .filter(r -> r.getDeletedAt() == null)
                .orElseThrow(() -> new RuntimeException("Financial record not found"));
        updateEntityFromDTO(record, dto);
        record.setUpdatedAt(LocalDateTime.now());
        record = financialRecordRepository.save(record);
        return toDTO(record);
    }

    @Transactional
    public void delete(UUID id) {
        logger.info("Soft deleting financial record with id: {}", id);
        FinancialRecord record = financialRecordRepository.findById(id)
                .filter(r -> r.getDeletedAt() == null)
                .orElseThrow(() -> new RuntimeException("Financial record not found"));
        record.setDeletedAt(LocalDateTime.now());
        financialRecordRepository.save(record);
    }

    @Transactional(readOnly = true)
    public FinancialSummaryDTO getSummary() {
        logger.info("Fetching financial summary");
        double income = financialRecordRepository.findAll()
                .stream()
                .filter(r -> r.getDeletedAt() == null && "income".equals(r.getType()))
                .mapToDouble(FinancialRecord::getAmount)
                .sum();
        double expense = financialRecordRepository.findAll()
                .stream()
                .filter(r -> r.getDeletedAt() == null && "expense".equals(r.getType()))
                .mapToDouble(FinancialRecord::getAmount)
                .sum();
        FinancialSummaryDTO summary = new FinancialSummaryDTO();
        summary.setIncome(income);
        summary.setExpense(expense);
        return summary;
    }

    private FinancialRecordDTO toDTO(FinancialRecord record) {
        FinancialRecordDTO dto = new FinancialRecordDTO();
        dto.setId(record.getId());
        dto.setType(record.getType());
        dto.setCategory(record.getCategory());
        dto.setAmount(record.getAmount());
        dto.setDescription(record.getDescription());
        dto.setDate(record.getDate());
        dto.setOwnerId(record.getOwnerId());
        dto.setAnimalId(record.getAnimalId());
        dto.setActivityId(record.getActivityId());
        dto.setPaymentMethod(record.getPaymentMethod());
        dto.setReceiptNumber(record.getReceiptNumber());
        dto.setReceiptImage(record.getReceiptImage());
        dto.setCreatedBy(record.getCreatedBy());
        dto.setCreatedAt(record.getCreatedAt());
        dto.setUpdatedAt(record.getUpdatedAt());
        dto.setDeletedAt(record.getDeletedAt());
        return dto;
    }

    private void updateEntityFromDTO(FinancialRecord record, FinancialRecordDTO dto) {
        record.setType(dto.getType());
        record.setCategory(dto.getCategory());
        record.setAmount(dto.getAmount());
        record.setDescription(dto.getDescription());
        record.setDate(dto.getDate());
        record.setOwnerId(dto.getOwnerId());
        record.setAnimalId(dto.getAnimalId());
        record.setActivityId(dto.getActivityId());
        record.setPaymentMethod(dto.getPaymentMethod());
        record.setReceiptNumber(dto.getReceiptNumber());
        record.setReceiptImage(dto.getReceiptImage());
        record.setCreatedBy(dto.getCreatedBy());
    }
}