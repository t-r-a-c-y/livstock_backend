package com.livestock.controller;

import com.livestock.dto.ApiResponse;
import com.livestock.service.AnimalService;
import com.livestock.service.FinancialRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final AnimalService animalService;
    private final FinancialRecordService financialRecordService;

    @GetMapping("/stats")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','STAFF')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();

        // Animal stats
        stats.put("totalAnimals", animalService.getAllAnimals().size());
        stats.put("sickAnimals", animalService.getSickAnimals().size());

        // Financial stats (last 30 days example)
        LocalDate today = LocalDate.now();
        LocalDate thirtyDaysAgo = today.minusDays(30);
        BigDecimal income = financialRecordService.getTotalIncome(thirtyDaysAgo, today);
        BigDecimal expense = financialRecordService.getTotalExpense(thirtyDaysAgo, today);
        stats.put("last30DaysIncome", income != null ? income : BigDecimal.ZERO);
        stats.put("last30DaysExpense", expense != null ? expense : BigDecimal.ZERO);
        stats.put("last30DaysProfit", income.subtract(expense));

        return ResponseEntity.ok(ApiResponse.success(stats, "Dashboard statistics"));
    }
}