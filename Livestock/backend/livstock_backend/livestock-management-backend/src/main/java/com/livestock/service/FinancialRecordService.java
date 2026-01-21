// FinancialRecordService.java
package com.livestock.service;

import com.livestock.dto.FinancialRecordDto;
import com.livestock.entity.FinancialRecord;
import com.livestock.entity.User;
import com.livestock.entity.enums.FinancialType;
import com.livestock.repository.FinancialRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FinancialRecordService {

    private final FinancialRecordRepository financialRecordRepository;
    private final UserService userService;

    @Transactional
    public FinancialRecordDto createFinancialRecord(FinancialRecordDto dto, UUID currentUserId) {
        FinancialRecord record = mapToEntity(dto);
        record.setCreatedBy(userService.getUserEntity(currentUserId));
        FinancialRecord saved = financialRecordRepository.save(record);
        return mapToDto(saved);
    }

    @Transactional
    public FinancialRecordDto updateFinancialRecord(UUID id, FinancialRecordDto dto) {
        FinancialRecord existing = financialRecordRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Record not found"));
        updateFields(existing, dto);
        return mapToDto(financialRecordRepository.save(existing));
    }

    @Transactional(readOnly = true)
    public FinancialRecordDto getFinancialRecordById(UUID id) {
        return mapToDto(financialRecordRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Record not found")));
    }

    @Transactional(readOnly = true)
    public List<FinancialRecordDto> getAllFinancialRecords() {
        return financialRecordRepository.findAll().stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Transactional
    public void deleteFinancialRecord(UUID id) {
        financialRecordRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<FinancialRecordDto> getByOwner(UUID ownerId) {
        return financialRecordRepository.findByOwnerId(ownerId).stream()
                .map(this::mapToDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public BigDecimal getTotalIncome(LocalDate start, LocalDate end) {
        return financialRecordRepository.sumIncomeBetween(start, end);
    }

    @Transactional(readOnly = true)
    public BigDecimal getTotalExpense(LocalDate start, LocalDate end) {
        return financialRecordRepository.sumExpenseBetween(start, end);
    }

    private void updateFields(FinancialRecord target, FinancialRecordDto source) {
        target.setType(source.getType());
        target.setCategory(source.getCategory());
        target.setAmount(source.getAmount());
        target.setDescription(source.getDescription());
        target.setDate(source.getDate());
        // owner, animal, paymentMethod, etc. can be updated similarly
    }

    private FinancialRecord mapToEntity(FinancialRecordDto dto) {
        FinancialRecord fr = new FinancialRecord();
        fr.setType(dto.getType());
        fr.setCategory(dto.getCategory());
        fr.setAmount(dto.getAmount());
        fr.setDescription(dto.getDescription());
        fr.setDate(dto.getDate());
        // map owner, animal, etc. if needed
        return fr;
    }

    private FinancialRecordDto mapToDto(FinancialRecord fr) {
        FinancialRecordDto dto = new FinancialRecordDto();
        dto.setId(fr.getId());
        dto.setType(fr.getType());
        dto.setCategory(fr.getCategory());
        dto.setAmount(fr.getAmount());
        dto.setDescription(fr.getDescription());
        dto.setDate(fr.getDate());
        dto.setCreatedAt(fr.getCreatedAt());
        dto.setUpdatedAt(fr.getUpdatedAt());
        // add ownerId, animalId etc.
        return dto;
    }
}