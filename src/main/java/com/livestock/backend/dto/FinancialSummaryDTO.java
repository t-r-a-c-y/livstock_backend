package com.livestock.backend.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class FinancialSummaryDTO {
    private BigDecimal totalIncome;
    private BigDecimal totalExpense;
    private BigDecimal net;

    public FinancialSummaryDTO(BigDecimal totalIncome, BigDecimal totalExpense, BigDecimal net) {
        this.totalIncome = totalIncome;
        this.totalExpense = totalExpense;
        this.net = net;
    }
}