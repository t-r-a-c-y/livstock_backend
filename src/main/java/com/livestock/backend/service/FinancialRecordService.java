package com.livestock.backend.service;

import com.livestock.backend.dto.FinancialRecordDTO;
import com.livestock.backend.dto.FinancialSummaryDTO;
import com.livestock.backend.model.FinancialRecord;
import com.livestock.backend.repository.FinancialRecordRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
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
    public Page<FinancialRecordDTO> getAll(String type, LocalDate startDate, LocalDate endDate, Pageable pageable) {
        logger.info("Fetching financial records with filters");
        Specification<FinancialRecord> spec = Specification.where((root, query, cb) -> cb.isNull(root.get("deletedAt")));
        if (type != null) spec = spec.and((root, query, cb) -> cb.equal(root.get("type"), type));
        if (startDate != null) spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("date"), startDate));
        if (endDate != null) spec = spec.and((root, query, cb) -> cb.lessThanOrEqualTo(root.get("date"), endDate));
        return financialRecordRepository.findAll(spec, pageable).map(this::toDTO);
    }

    @Transactional(readOnly = true)
    public FinancialRecordDTO getById(UUID id) {
        logger.info("Fetching financial record by ID: {}", id);
        FinancialRecord record = financialRecordRepository.findById(id).orElseThrow(() -> new RuntimeException("Record not found"));
        if (record.getDeletedAt() != null) throw new RuntimeException("Record deleted");
        return toDTO(record);
    }

    @Transactional
    public FinancialRecordDTO create(FinancialRecordDTO dto) {
        logger.info("Creating financial record: {}", dto.getType());
        FinancialRecord record = new FinancialRecord();
        mapToEntity(dto, record);
        record.setCreatedBy(getCurrentUserId());
        record.setCreatedAt(LocalDateTime.now());
        record.setUpdatedAt(LocalDateTime.now());
        record = financialRecordRepository.save(record);
        return toDTO(record);
    }

    @Transactional
    public FinancialRecordDTO update(UUID id, FinancialRecordDTO dto) {
        logger.info("Updating financial record: {}", id);
        FinancialRecord record = financialRecordRepository.findById(id).orElseThrow(() -> new RuntimeException("Record not found"));
        if (record.getDeletedAt() != null) throw new RuntimeException("Record deleted");
        mapToEntity(dto, record);
        record.setUpdatedAt(LocalDateTime.now());
        record = financialRecordRepository.save(record);
        return toDTO(record);
    }

    @Transactional
    public void softDelete(UUID id) {
        logger.info("Soft deleting financial record: {}", id);
        FinancialRecord record = financialRecordRepository.findById(id).orElseThrow(() -> new RuntimeException("Record not found"));
        record.setDeletedAt(LocalDateTime.now());
        financialRecordRepository.save(record);
    }

    @Transactional(readOnly = true)
    @Cacheable("financialSummary")
    public FinancialSummaryDTO getSummary() {
        logger.info("Fetching financial summary");
        return financialRecordRepository.getSummary();
    }

    private FinancialRecordDTO toDTO(FinancialRecord record) {
        FinancialRecordDTO dto = new FinancialRecordDTO();
        dto.setId(record.getId());
        dto.setType(record.getType());
        dto.setDate(record.getDate());
        dto.setAmount(record.getAmount());
        dto.setDescription(record.getDescription());
        dto.setPaymentMethod(record.getPaymentMethod());
        dto.setReceiptNumber(record.getReceiptNumber());
        dto.setReceiptImage(record.getReceiptImage());
        dto.setActivityId(record.getActivityId());
        dto.setCreatedBy(record.getCreatedBy());
        dto.setCreatedAt(record.getCreatedAt());
        dto.setUpdatedAt(record.getUpdatedAt());
        return dto;
    }

    private void mapToEntity(FinancialRecordDTO dto, FinancialRecord record) {
        record.setType(dto.getType());
        record.setDate(dto.getDate());
        record.setAmount(dto.getAmount());
        record.setDescription(dto.getDescription());
        record.setPaymentMethod(dto.getPaymentMethod());
        record.setReceiptNumber(dto.getReceiptNumber());
        record.setReceiptImage(dto.getReceiptImage());
        record.setActivityId(dto.getActivityId());
    }

    private UUID getCurrentUserId() {
        // same as above
    }
}