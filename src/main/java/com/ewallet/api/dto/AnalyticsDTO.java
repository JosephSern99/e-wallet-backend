package com.ewallet.api.dto;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Data
@Getter
@Setter
public class AnalyticsDTO {

    public enum TimeFrame {
        DAILY, WEEKLY, MONTHLY, YEARLY
    }

    public static class CategorySpending {
        private String category;
        private BigDecimal amount;
        private double percentage;

        // Getters, setters, constructors
    }

    public static class SpendingOverTime {
        private LocalDate date;
        private BigDecimal income;
        private BigDecimal expense;
        private BigDecimal balance;

        // Getters, setters, constructors
    }

    public static class TransactionSummary {
        private BigDecimal totalIncome;
        private BigDecimal totalExpense;
        private BigDecimal netChange;
        private int transactionCount;
        private Map<String, Integer> transactionsByType;
    }

}
