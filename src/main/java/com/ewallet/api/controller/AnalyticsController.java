package com.ewallet.api.controller;

import com.ewallet.api.dto.AnalyticsDTO;
import com.ewallet.api.security.AuthenticationContext;
import com.ewallet.api.service.AnalyticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @Autowired
    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/spending-by-category")
    public ResponseEntity<Map<String, Object>> getSpendingByCategory(
            @RequestParam(required = false) Long walletId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate, AuthenticationContext authenticationContext) {
        return ResponseEntity.ok(analyticsService.getSpendingByCategory(walletId, startDate, endDate, authenticationContext));
    }

    @GetMapping("/spending-over-time")
    public ResponseEntity<Map<String, Object>> getSpendingOverTime(
            @RequestParam(required = false) Long walletId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "DAILY") AnalyticsDTO.TimeFrame timeFrame) {
        return ResponseEntity.ok(analyticsService.getSpendingOverTime(walletId, startDate, endDate, timeFrame));
    }

    @GetMapping("/income-expense-ratio")
    public ResponseEntity<Map<String, Object>> getIncomeExpenseRatio(
            @RequestParam(required = false) Long walletId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(analyticsService.getIncomeExpenseRatio(walletId, startDate, endDate));
    }

    @GetMapping("/transaction-summary")
    public ResponseEntity<Map<String, Object>> getTransactionSummary(
            @RequestParam(required = false) Long walletId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        return ResponseEntity.ok(analyticsService.getTransactionSummary(walletId, startDate, endDate));
    }
}
