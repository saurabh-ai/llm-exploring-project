package com.javamastery.streams.processor;

import com.javamastery.streams.model.Transaction;
import com.javamastery.streams.collector.CustomCollectors;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Processor for Transaction data demonstrating financial data analysis with streams
 * Shows complex filtering, grouping, and aggregation operations
 */
public class TransactionProcessor {
    
    private final List<Transaction> transactions;
    
    public TransactionProcessor(List<Transaction> transactions) {
        this.transactions = Objects.requireNonNull(transactions, "Transactions list cannot be null");
    }
    
    /**
     * Calculate total amount by transaction type
     */
    public Map<Transaction.TransactionType, Double> getTotalByType() {
        return transactions.stream()
            .collect(Collectors.groupingBy(
                Transaction::type,
                Collectors.summingDouble(Transaction::amount)
            ));
    }
    
    /**
     * Find all large transactions (amount >= 1000) sorted by amount
     */
    public List<Transaction> getLargeTransactions() {
        return transactions.stream()
            .filter(Transaction::isLargeTransaction)
            .sorted(Comparator.comparing(Transaction::amount).reversed())
            .collect(Collectors.toList());
    }
    
    /**
     * Group transactions by account and calculate balance changes
     */
    public Map<String, Double> getAccountBalanceChanges() {
        return transactions.stream()
            .collect(Collectors.groupingBy(
                Transaction::accountId,
                Collectors.summingDouble(transaction -> 
                    transaction.type() == Transaction.TransactionType.CREDIT 
                        ? transaction.amount() 
                        : -transaction.amount()
                )
            ));
    }
    
    /**
     * Get transaction statistics using custom collector
     */
    public CustomCollectors.Statistics getTransactionStatistics() {
        return transactions.stream()
            .collect(CustomCollectors.toStatistics(Transaction::amount));
    }
    
    /**
     * Find suspicious transaction patterns (multiple large transactions from same account)
     */
    public Map<String, Long> findSuspiciousPatterns() {
        return transactions.stream()
            .filter(Transaction::isLargeTransaction)
            .collect(CustomCollectors.groupingByWithMinCount(Transaction::accountId, 3));
    }
    
    /**
     * Analyze transaction frequency by hour of day
     */
    public Map<Integer, Long> getTransactionsByHour() {
        return transactions.stream()
            .collect(Collectors.groupingBy(
                transaction -> transaction.timestamp().getHour(),
                Collectors.counting()
            ))
            .entrySet().stream()
            .sorted(Map.Entry.comparingByKey())
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                Map.Entry::getValue,
                (e1, e2) -> e1,
                LinkedHashMap::new
            ));
    }
    
    /**
     * Calculate daily transaction volume
     */
    public Map<LocalDate, Double> getDailyVolume() {
        return transactions.stream()
            .collect(Collectors.groupingBy(
                transaction -> transaction.timestamp().toLocalDate(),
                Collectors.summingDouble(Transaction::amount)
            ))
            .entrySet().stream()
            .sorted(Map.Entry.comparingByKey())
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                Map.Entry::getValue,
                (e1, e2) -> e1,
                LinkedHashMap::new
            ));
    }
    
    /**
     * Group transactions into buckets by amount ranges
     */
    public Map<String, List<Transaction>> groupByAmountRanges() {
        return transactions.stream()
            .collect(CustomCollectors.toBuckets(
                Transaction::amount, 
                100.0, 500.0, 1000.0, 5000.0
            ));
    }
    
    /**
     * Find top spending categories
     */
    public List<Map.Entry<String, Double>> getTopSpendingCategories(int limit) {
        return transactions.stream()
            .filter(Transaction::isExpense)
            .collect(Collectors.groupingBy(
                Transaction::category,
                Collectors.summingDouble(Transaction::amount)
            ))
            .entrySet().stream()
            .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
            .limit(limit)
            .collect(Collectors.toList());
    }
    
    /**
     * Calculate account activity metrics
     */
    public Map<String, AccountActivity> getAccountActivity() {
        return transactions.stream()
            .collect(Collectors.groupingBy(Transaction::accountId))
            .entrySet().stream()
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                entry -> new AccountActivity(entry.getValue())
            ));
    }
    
    /**
     * Find transactions within date range with complex filtering
     */
    public List<Transaction> getTransactionsInDateRange(LocalDateTime start, LocalDateTime end) {
        return transactions.stream()
            .filter(transaction -> transaction.timestamp().isAfter(start) 
                && transaction.timestamp().isBefore(end))
            .sorted(Comparator.comparing(Transaction::timestamp))
            .collect(Collectors.toList());
    }
    
    /**
     * Generate fraud detection report using parallel streams
     */
    public FraudDetectionReport generateFraudDetectionReport() {
        // Process in parallel for performance
        Map<String, List<Transaction>> accountTransactions = transactions.parallelStream()
            .collect(Collectors.groupingBy(Transaction::accountId));
        
        Set<String> suspiciousAccounts = accountTransactions.entrySet().parallelStream()
            .filter(entry -> entry.getValue().size() > 50) // High transaction volume
            .filter(entry -> entry.getValue().stream()
                .anyMatch(t -> t.amount() > 10000)) // Has large transactions
            .map(Map.Entry::getKey)
            .collect(Collectors.toSet());
        
        List<Transaction> highRiskTransactions = transactions.parallelStream()
            .filter(t -> t.amount() > 5000)
            .filter(t -> t.timestamp().isAfter(LocalDateTime.now().minusDays(7)))
            .sorted(Comparator.comparing(Transaction::amount).reversed())
            .collect(Collectors.toList());
        
        return new FraudDetectionReport(suspiciousAccounts, highRiskTransactions);
    }
    
    /**
     * Account activity summary class
     */
    public static class AccountActivity {
        private final int transactionCount;
        private final double totalAmount;
        private final double averageAmount;
        private final LocalDateTime firstTransaction;
        private final LocalDateTime lastTransaction;
        
        public AccountActivity(List<Transaction> transactions) {
            this.transactionCount = transactions.size();
            this.totalAmount = transactions.stream().mapToDouble(Transaction::amount).sum();
            this.averageAmount = totalAmount / transactionCount;
            this.firstTransaction = transactions.stream()
                .min(Comparator.comparing(Transaction::timestamp))
                .map(Transaction::timestamp)
                .orElse(null);
            this.lastTransaction = transactions.stream()
                .max(Comparator.comparing(Transaction::timestamp))
                .map(Transaction::timestamp)
                .orElse(null);
        }
        
        // Getters
        public int getTransactionCount() { return transactionCount; }
        public double getTotalAmount() { return totalAmount; }
        public double getAverageAmount() { return averageAmount; }
        public LocalDateTime getFirstTransaction() { return firstTransaction; }
        public LocalDateTime getLastTransaction() { return lastTransaction; }
        
        @Override
        public String toString() {
            return String.format("AccountActivity{count=%d, total=%.2f, avg=%.2f}", 
                transactionCount, totalAmount, averageAmount);
        }
    }
    
    /**
     * Fraud detection report class
     */
    public static class FraudDetectionReport {
        private final Set<String> suspiciousAccounts;
        private final List<Transaction> highRiskTransactions;
        
        public FraudDetectionReport(Set<String> suspiciousAccounts, List<Transaction> highRiskTransactions) {
            this.suspiciousAccounts = suspiciousAccounts;
            this.highRiskTransactions = highRiskTransactions;
        }
        
        public Set<String> getSuspiciousAccounts() { return suspiciousAccounts; }
        public List<Transaction> getHighRiskTransactions() { return highRiskTransactions; }
        
        @Override
        public String toString() {
            return String.format("FraudReport{suspiciousAccounts=%d, highRiskTransactions=%d}", 
                suspiciousAccounts.size(), highRiskTransactions.size());
        }
    }
}