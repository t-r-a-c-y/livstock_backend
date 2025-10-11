package com.livestock.backend.service;

import com.livestock.backend.dto.FinancialRecordDTO;
import com.livestock.backend.dto.FinancialRecordCreateDTO;
import com.livestock.backend.dto.FinancialRecordUpdateDTO;
import com.livestock.backend.dto.FinancialSummaryDTO;
import com.livestock.backend.model.FinancialRecord;
import com.livestock.backend.repository.FinancialRecordRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FinancialRecordService {
    private static final Logger logger = LoggerFactory.getLogger(FinancialRecordService.class);
    private final FinancialRecordRepository financialRecordRepository;
    private final ModelMapper modelMapper;

    public Page<FinancialRecordDTO> getAllFinancialRecords(String type, LocalDate startDate, LocalDate endDate, Pageable pageable) {
        logger.info("Fetching financial records with filters: type={}, startDate={}, endDate={}", type, startDate, endDate);
        return financialRecordRepository.findByFilters(type, startDate, endDate, pageable)
                .map(record -> modelMapper.map(record, FinancialRecordDTO.class));
    }

    public FinancialRecordDTO getFinancialRecordById(UUID id) {
        logger.info("Fetching financial record with id: {}", id);
        FinancialRecord record = financialRecordRepository.findById(id)
                .filter(r -> r.getDeletedAt() == null)
                .orElseThrow(() -> new RuntimeException("Financial record not found or deleted"));
        return modelMapper.map(record, FinancialRecordDTO.class);
    }

    @Transactional
    public FinancialRecordDTO createFinancialRecord(FinancialRecordCreateDTO dto) {
        logger.info("Creating new financial record of type: {}", dto.getType());
        FinancialRecord record = modelMapper.map(dto, FinancialRecord.class);
        record.setCreatedAt(LocalDateTime.now());
        record.setUpdatedAt(LocalDateTime.now());
        record = financialRecordRepository.save(record);
        return modelMapper.map(record, FinancialRecordDTO.class);
    }

    @Transactional
    public FinancialRecordDTO updateFinancialRecord(UUID id, FinancialRecordUpdateDTO dto) {
        logger.info("Updating financial record with id: {}", id);
        FinancialRecord record = financialRecordRepository.findById(id)
                .filter(r -> r.getDeletedAt() == null)
                .orElseThrow(() -> new RuntimeException("Financial record not found or deleted"));
        modelMapper.map(dto, record);
        record.setUpdatedAt(LocalDateTime.now());
        record = financialRecordRepository.save(record);
        return modelMapper.map(record, FinancialRecordDTO.class);
    }

    @Transactional
    public void softDeleteFinancialRecord(UUID id) {
        logger.info("Soft deleting financial record with id: {}", id);
        FinancialRecord record = financialRecordRepository.findById(id)
                .filter(r -> r.getDeletedAt() == null)
                .orElseThrow(() -> new RuntimeException("Financial record not found or already deleted"));
        record.setDeletedAt(LocalDateTime.now());
        financialRecordRepository.save(record);
    }

    public FinancialSummaryDTO getFinancialSummary() {
        logger.info("Fetching financial summary");
        FinancialSummaryDTO summary = new FinancialSummaryDTO();
        Double income = financialRecordRepository.getIncomeSummary();
        Double expense = financialRecordRepository.getExpenseSummary();
        summary.setTotalIncome(income != null ? income : 0.0);
        summary.setTotalExpense(expense != null ? expense : 0.0);
        summary.setNetBalance(summary.getTotalIncome() - summary.getTotalExpense());
        return summary;
    }
}