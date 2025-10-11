package com.livestock.backend.dto;

import lombok.Data;

@Data
public class FinancialSummaryDTO {
    private Double totalIncome;
    private Double totalExpense;
    private Double netBalance;
}