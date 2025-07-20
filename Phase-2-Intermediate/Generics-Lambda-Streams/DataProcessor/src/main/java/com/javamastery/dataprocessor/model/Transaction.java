package com.javamastery.dataprocessor.model;

import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * Immutable record representing a financial transaction
 * Demonstrates various data types and validation patterns
 */
public record Transaction(
    String transactionId,
    String accountId,
    String customerId,
    TransactionType type,
    BigDecimal amount,
    String currency,
    LocalDateTime timestamp,
    String description,
    TransactionStatus status,
    String merchantCategory
) {
    
    public Transaction {
        Objects.requireNonNull(transactionId, "Transaction ID cannot be null");
        Objects.requireNonNull(accountId, "Account ID cannot be null");
        Objects.requireNonNull(type, "Transaction type cannot be null");
        Objects.requireNonNull(amount, "Amount cannot be null");
        Objects.requireNonNull(timestamp, "Timestamp cannot be null");
        Objects.requireNonNull(status, "Status cannot be null");
        
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
    }
    
    public enum TransactionType {
        DEPOSIT, WITHDRAWAL, TRANSFER, PAYMENT, REFUND
    }
    
    public enum TransactionStatus {
        PENDING, COMPLETED, FAILED, CANCELLED
    }
    
    // Functional methods for stream operations
    public boolean isLargeTransaction() {
        return amount.compareTo(new BigDecimal("10000")) > 0;
    }
    
    public boolean isCompleted() {
        return status == TransactionStatus.COMPLETED;
    }
    
    public boolean isDebit() {
        return type == TransactionType.WITHDRAWAL || type == TransactionType.PAYMENT;
    }
    
    public boolean isCredit() {
        return type == TransactionType.DEPOSIT || type == TransactionType.REFUND;
    }
    
    public int getHourOfDay() {
        return timestamp.getHour();
    }
    
    public String getDayOfWeek() {
        return timestamp.getDayOfWeek().toString();
    }
}