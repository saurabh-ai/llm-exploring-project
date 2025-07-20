package com.javamastery.streams.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Immutable data model representing a transaction record
 * Used for demonstrating financial data processing with streams
 */
public record Transaction(
    String transactionId,
    String accountId,
    TransactionType type,
    double amount,
    String category,
    String description,
    LocalDateTime timestamp,
    String currency
) {
    
    /**
     * Transaction types
     */
    public enum TransactionType {
        DEBIT, CREDIT, TRANSFER, FEE
    }
    
    /**
     * Compact constructor for validation
     */
    public Transaction {
        Objects.requireNonNull(transactionId, "Transaction ID cannot be null");
        Objects.requireNonNull(accountId, "Account ID cannot be null");
        Objects.requireNonNull(type, "Transaction type cannot be null");
        Objects.requireNonNull(category, "Category cannot be null");
        Objects.requireNonNull(description, "Description cannot be null");
        Objects.requireNonNull(timestamp, "Timestamp cannot be null");
        Objects.requireNonNull(currency, "Currency cannot be null");
        
        if (amount < 0) {
            throw new IllegalArgumentException("Amount cannot be negative");
        }
    }
    
    /**
     * Check if this is a large transaction (amount >= 1000)
     */
    public boolean isLargeTransaction() {
        return amount >= 1000.0;
    }
    
    /**
     * Check if this is an expense transaction
     */
    public boolean isExpense() {
        return type == TransactionType.DEBIT || type == TransactionType.FEE;
    }
    
    /**
     * Check if this is an income transaction
     */
    public boolean isIncome() {
        return type == TransactionType.CREDIT;
    }
    
    /**
     * Get formatted amount string
     */
    public String formattedAmount() {
        return String.format("%.2f %s", amount, currency);
    }
}