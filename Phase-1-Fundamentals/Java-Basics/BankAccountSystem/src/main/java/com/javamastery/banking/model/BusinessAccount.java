package com.javamastery.banking.model;

import com.javamastery.banking.exception.InsufficientFundsException;
import com.javamastery.banking.exception.WithdrawalLimitExceededException;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Business account implementation for commercial banking.
 * Features:
 * - $10,000 minimum balance requirement
 * - 1.5% annual interest rate
 * - No overdraft protection
 * - Business-specific information
 */
public class BusinessAccount extends Account {
    private static final BigDecimal MINIMUM_BALANCE = new BigDecimal("10000.00");
    private static final BigDecimal INTEREST_RATE = new BigDecimal("0.015"); // 1.5%
    
    private String businessName;
    private String taxId;
    private BigDecimal minimumBalance;
    private BigDecimal interestRate;

    /**
     * Constructor for BusinessAccount.
     *
     * @param accountNumber    Account number
     * @param accountHolderName Account holder name (business owner/authorized person)
     * @param businessName     Legal business name
     * @param taxId           Business tax identification number
     * @param initialBalance   Initial deposit
     */
    public BusinessAccount(String accountNumber, String accountHolderName, String businessName, 
                          String taxId, BigDecimal initialBalance) {
        super(accountNumber, accountHolderName, initialBalance);
        this.businessName = businessName;
        this.taxId = taxId;
        this.minimumBalance = MINIMUM_BALANCE;
        this.interestRate = INTEREST_RATE;
    }

    /**
     * Calculates annual interest based on current balance.
     *
     * @return Interest amount
     */
    @Override
    public BigDecimal calculateInterest() {
        return getBalance().multiply(interestRate).setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public String getAccountType() {
        return "Business";
    }

    /**
     * Withdraw with minimum balance restriction. No overdraft protection.
     *
     * @param amount Amount to withdraw
     * @throws InsufficientFundsException if insufficient funds or would violate minimum balance
     */
    @Override
    public void withdraw(BigDecimal amount) throws InsufficientFundsException, WithdrawalLimitExceededException {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be positive");
        }

        // Check if withdrawal would violate minimum balance
        BigDecimal balanceAfterWithdrawal = getBalance().subtract(amount);
        if (balanceAfterWithdrawal.compareTo(minimumBalance) < 0) {
            throw new InsufficientFundsException(
                String.format("Withdrawal would violate minimum balance requirement of %.2f. Available for withdrawal: %.2f",
                    minimumBalance, getBalance().subtract(minimumBalance)));
        }

        // Perform withdrawal
        setBalance(balanceAfterWithdrawal);
    }

    /**
     * Applies interest to the account balance.
     */
    public void applyInterest() {
        BigDecimal interest = calculateInterest();
        setBalance(getBalance().add(interest));
    }

    /**
     * Gets the available amount that can be withdrawn without violating minimum balance.
     *
     * @return Available withdrawal amount
     */
    public BigDecimal getAvailableForWithdrawal() {
        BigDecimal available = getBalance().subtract(minimumBalance);
        return available.compareTo(BigDecimal.ZERO) > 0 ? available : BigDecimal.ZERO;
    }

    /**
     * Checks if minimum balance requirement is met.
     *
     * @return true if balance meets minimum requirement
     */
    public boolean meetsMinimumBalance() {
        return getBalance().compareTo(minimumBalance) >= 0;
    }

    // Getters and Setters
    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String getTaxId() {
        return taxId;
    }

    public void setTaxId(String taxId) {
        this.taxId = taxId;
    }

    public BigDecimal getMinimumBalance() {
        return minimumBalance;
    }

    public BigDecimal getInterestRate() {
        return interestRate;
    }

    @Override
    public String toString() {
        return String.format("BusinessAccount{Number='%s', Holder='%s', Business='%s', TaxId='%s', Balance=%.2f, InterestRate=%.3f%%, MinBalance=%.2f}",
                getAccountNumber(), getAccountHolderName(), businessName, taxId, getBalance(), 
                interestRate.multiply(new BigDecimal("100")), minimumBalance);
    }
}