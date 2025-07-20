package com.javamastery.banking.model;

import com.javamastery.banking.exception.InsufficientFundsException;
import com.javamastery.banking.exception.WithdrawalLimitExceededException;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Abstract base class for all bank accounts.
 * Provides common functionality and enforces implementation of account-specific methods.
 */
public abstract class Account {
    private String accountNumber;
    private String accountHolderName;
    private BigDecimal balance;
    private LocalDateTime createdDate;

    /**
     * Constructor for creating a new account.
     *
     * @param accountNumber    Auto-generated 8-digit account number
     * @param accountHolderName Name of the account holder
     * @param initialBalance   Initial deposit amount
     */
    public Account(String accountNumber, String accountHolderName, BigDecimal initialBalance) {
        this.accountNumber = accountNumber;
        this.accountHolderName = accountHolderName;
        this.balance = initialBalance != null ? initialBalance : BigDecimal.ZERO;
        this.createdDate = LocalDateTime.now();
    }

    /**
     * Calculates interest for the account based on account type.
     *
     * @return Interest amount
     */
    public abstract BigDecimal calculateInterest();

    /**
     * Returns the account type as a string.
     *
     * @return Account type
     */
    public abstract String getAccountType();

    /**
     * Deposits money into the account.
     *
     * @param amount Amount to deposit
     * @throws IllegalArgumentException if amount is null or negative
     */
    public void deposit(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Deposit amount must be positive");
        }
        this.balance = this.balance.add(amount);
    }

    /**
     * Withdraws money from the account.
     * Basic implementation - can be overridden by subclasses for specific rules.
     *
     * @param amount Amount to withdraw
     * @throws InsufficientFundsException if insufficient funds
     * @throws WithdrawalLimitExceededException if withdrawal limits are exceeded
     * @throws IllegalArgumentException if amount is null or negative
     */
    public void withdraw(BigDecimal amount) throws InsufficientFundsException, WithdrawalLimitExceededException {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be positive");
        }
        if (balance.compareTo(amount) < 0) {
            throw new InsufficientFundsException("Insufficient funds for withdrawal. Available: " + balance);
        }
        this.balance = this.balance.subtract(amount);
    }

    // Getters and Setters
    public String getAccountNumber() {
        return accountNumber;
    }

    public String getAccountHolderName() {
        return accountHolderName;
    }

    public void setAccountHolderName(String accountHolderName) {
        this.accountHolderName = accountHolderName;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    protected void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    @Override
    public String toString() {
        return String.format("Account{Number='%s', Holder='%s', Type='%s', Balance=%.2f, Created=%s}",
                accountNumber, accountHolderName, getAccountType(), balance, createdDate.toLocalDate());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Account account = (Account) obj;
        return accountNumber.equals(account.accountNumber);
    }

    @Override
    public int hashCode() {
        return accountNumber.hashCode();
    }
}