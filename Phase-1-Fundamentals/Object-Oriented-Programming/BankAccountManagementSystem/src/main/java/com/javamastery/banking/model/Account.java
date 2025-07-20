package com.javamastery.banking.model;

import com.javamastery.banking.exception.InsufficientFundsException;
import com.javamastery.banking.exception.WithdrawalLimitExceededException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Abstract base class for all bank accounts.
 * Provides common functionality and enforces implementation of account-specific methods.
 */
public abstract class Account {
    
    private static final AtomicLong ACCOUNT_NUMBER_GENERATOR = new AtomicLong(10000000L);
    
    protected final String accountNumber;
    protected final String accountHolderName;
    protected BigDecimal balance;
    protected final LocalDateTime createdDate;
    
    /**
     * Constructs a new Account with the specified account holder name and initial balance.
     * 
     * @param accountHolderName the name of the account holder
     * @param initialBalance the initial balance (must be non-negative)
     * @throws IllegalArgumentException if accountHolderName is null/empty or initialBalance is negative
     */
    public Account(String accountHolderName, BigDecimal initialBalance) {
        if (accountHolderName == null || accountHolderName.trim().isEmpty()) {
            throw new IllegalArgumentException("Account holder name cannot be null or empty");
        }
        if (initialBalance == null || initialBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Initial balance cannot be null or negative");
        }
        
        this.accountNumber = String.valueOf(ACCOUNT_NUMBER_GENERATOR.getAndIncrement());
        this.accountHolderName = accountHolderName.trim();
        this.balance = initialBalance.setScale(2, RoundingMode.HALF_UP);
        this.createdDate = LocalDateTime.now();
    }
    
    /**
     * Deposits the specified amount to this account.
     * 
     * @param amount the amount to deposit (must be positive)
     * @throws IllegalArgumentException if amount is null or not positive
     */
    public void deposit(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Deposit amount must be positive");
        }
        
        this.balance = this.balance.add(amount.setScale(2, RoundingMode.HALF_UP));
        System.out.println("Deposited $" + amount + " to account " + accountNumber + 
                          ". New balance: $" + balance);
    }
    
    /**
     * Withdraws the specified amount from this account.
     * Subclasses may override to implement specific withdrawal rules.
     * 
     * @param amount the amount to withdraw (must be positive)
     * @throws IllegalArgumentException if amount is null or not positive
     * @throws InsufficientFundsException if the account has insufficient funds
     * @throws WithdrawalLimitExceededException if withdrawal limits are exceeded
     */
    public void withdraw(BigDecimal amount) throws InsufficientFundsException, WithdrawalLimitExceededException {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be positive");
        }
        
        if (balance.compareTo(amount) < 0) {
            throw new InsufficientFundsException("Insufficient funds. Balance: $" + balance + 
                                               ", Attempted withdrawal: $" + amount);
        }
        
        this.balance = this.balance.subtract(amount.setScale(2, RoundingMode.HALF_UP));
        System.out.println("Withdrew $" + amount + " from account " + accountNumber + 
                          ". New balance: $" + balance);
    }
    
    /**
     * Calculates the interest for this account based on account-specific rules.
     * 
     * @return the calculated interest amount
     */
    public abstract BigDecimal calculateInterest();
    
    /**
     * Returns the type of this account.
     * 
     * @return the account type as a string
     */
    public abstract String getAccountType();
    
    // Getters
    public String getAccountNumber() { return accountNumber; }
    public String getAccountHolderName() { return accountHolderName; }
    public BigDecimal getBalance() { return balance; }
    public LocalDateTime getCreatedDate() { return createdDate; }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return Objects.equals(accountNumber, account.accountNumber);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(accountNumber);
    }
    
    @Override
    public String toString() {
        return String.format("%s [Account: %s, Holder: %s, Balance: $%s, Created: %s]",
                getAccountType(), accountNumber, accountHolderName, balance, 
                createdDate.toLocalDate());
    }
}