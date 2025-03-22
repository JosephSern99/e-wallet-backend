package com.ewallet.api.service;

import com.ewallet.api.dto.AnalyticsDTO;
import com.ewallet.api.model.Transaction;
import com.ewallet.api.model.enums.TransactionType;
import com.ewallet.api.repository.TransactionRepository;
import com.ewallet.api.repository.WalletRepository;
import com.ewallet.api.security.AuthenticationContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AnalyticsService {
    private final TransactionRepository transactionRepository;
    private final WalletRepository walletRepository;

    @Autowired
    public AnalyticsService(TransactionRepository transactionRepository, WalletRepository walletRepository) {
        this.transactionRepository = transactionRepository;
        this.walletRepository = walletRepository;
    }

    public Map<String, Object> getSpendingByCategory(Long walletId, LocalDate startDate, LocalDate endDate, AuthenticationContext authenticationContext) {
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.plusDays(1).atStartOfDay();

        List<Transaction> transactions;
        if (walletId != null) {
            transactions = transactionRepository.findByWalletIdAndCreatedAtBetweenAndType(
                    walletId, start, end, TransactionType.WITHDRAWAL);
        } else {
            // Get current user's transactions across all wallets
            transactions = transactionRepository.findByWalletUserIdAndCreatedAtBetweenAndType(
                    Long.valueOf(authenticationContext.getCurrentLoggedInUser().getId()), start, end, TransactionType.WITHDRAWAL);
        }

        // Process transactions to get spending by category
        Map<String, BigDecimal> categorySpending = new HashMap<>();
        BigDecimal totalSpending = BigDecimal.ZERO;

        for (Transaction tx : transactions) {
            String category = tx.getCategory() != null ? tx.getCategory().getName() : "Uncategorized";
            BigDecimal currentAmount = categorySpending.getOrDefault(category, BigDecimal.ZERO);
            categorySpending.put(category, currentAmount.add(tx.getAmount()));
            totalSpending = totalSpending.add(tx.getAmount());
        }

        // Calculate percentages
        BigDecimal finalTotalSpending = totalSpending;
        List<Map<String, Object>> result = categorySpending.entrySet().stream()
                .map(entry -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("category", entry.getKey());
                    item.put("amount", entry.getValue());
                    item.put("percentage", entry.getValue().divide(finalTotalSpending, 4, BigDecimal.ROUND_HALF_UP)
                            .multiply(new BigDecimal(100)));
                    return item;
                })
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("totalSpending", totalSpending);
        response.put("categories", result);

        return response;
    }

    public Map<String, Object> getSpendingOverTime(Long walletId, LocalDate startDate, LocalDate endDate,
                                                   AnalyticsDTO.TimeFrame timeFrame) {
        // Implementation for spending over time
        // Group transactions by time frame (day, week, month, year)
        // Return spending data for each time period

        return new HashMap<>(); // Placeholder
    }

    public Map<String, Object> getIncomeExpenseRatio(Long walletId, LocalDate startDate, LocalDate endDate) {
        // Implementation for income vs expense ratio
        return new HashMap<>(); // Placeholder
    }

    public Map<String, Object> getTransactionSummary(Long walletId, LocalDate startDate, LocalDate endDate) {
        // Implementation for transaction summary
        return new HashMap<>(); // Placeholder
    }
}
