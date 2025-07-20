package com.javamastery.banking.model;

import com.javamastery.banking.exception.InsufficientFundsException;
import com.javamastery.banking.exception.WithdrawalLimitExceededException;
import java.math.BigDecimal;

/**
 * Checking account implementation with overdraft protection.
 * Features:
 * - $500 overdraft limit
 * - $10 monthly fee
 * - No interest earned
 */
public class CheckingAccount extends Account {
    private static final BigDecimal OVERDRAFT_LIMIT = new BigDecimal("500.00");
    private static final BigDecimal MONTHLY_FEE = new BigDecimal("10.00");
    private BigDecimal overdraftLimit;
    private BigDecimal monthlyFee;

    /**
     * Constructor for CheckingAccount.
     *
     * @param accountNumber    Account number
     * @param accountHolderName Account holder name
     * @param initialBalance   Initial deposit
     */
    public CheckingAccount(String accountNumber, String accountHolderName, BigDecimal initialBalance) {
        super(accountNumber, accountHolderName, initialBalance);
        this.overdraftLimit = OVERDRAFT_LIMIT;
        this.monthlyFee = MONTHLY_FEE;
    }

    /**
     * Checking accounts don't earn interest.
     *
     * @return Zero interest
     */
    @Override
    public BigDecimal calculateInterest() {
        return BigDecimal.ZERO;
    }

    @Override
    public String getAccountType() {
        return "Checking";
    }

    /**
     * Withdraw with overdraft protection.
     * Allows withdrawal up to the overdraft limit.
     *
     * @param amount Amount to withdraw
     * @throws InsufficientFundsException if withdrawal exceeds balance + overdraft limit
     */
    @Override
    public void withdraw(BigDecimal amount) throws InsufficientFundsException, WithdrawalLimitExceededException {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be positive");
        }

        BigDecimal availableFunds = getBalance().add(overdraftLimit);
        if (amount.compareTo(availableFunds) > 0) {
            throw new InsufficientFundsException(
                String.format("Insufficient funds for withdrawal. Available: %.2f (Balance: %.2f + Overdraft: %.2f)",
                    availableFunds, getBalance(), overdraftLimit));
        }

        setBalance(getBalance().subtract(amount));
    }

    /**
     * Applies the monthly maintenance fee.
     */
    public void applyMonthlyFee() {
        setBalance(getBalance().subtract(monthlyFee));
    }

    /**
     * Checks if the account is overdrawn.
     *
     * @return true if balance is negative
     */
    public boolean isOverdrawn() {
        return getBalance().compareTo(BigDecimal.ZERO) < 0;
    }

    /**
     * Gets the amount overdrawn (positive number if overdrawn, zero otherwise).
     *
     * @return Amount overdrawn
     */
    public BigDecimal getOverdrawnAmount() {
        if (isOverdrawn()) {
            return getBalance().abs();
        }
        return BigDecimal.ZERO;
    }

    // Getters
    public BigDecimal getOverdraftLimit() {
        return overdraftLimit;
    }

    public BigDecimal getMonthlyFee() {
        return monthlyFee;
    }

    public BigDecimal getAvailableFunds() {
        return getBalance().add(overdraftLimit);
    }

    @Override
    public String toString() {
        return String.format("CheckingAccount{Number='%s', Holder='%s', Balance=%.2f, OverdraftLimit=%.2f, Available=%.2f}",
                getAccountNumber(), getAccountHolderName(), getBalance(), overdraftLimit, getAvailableFunds());
    }
}