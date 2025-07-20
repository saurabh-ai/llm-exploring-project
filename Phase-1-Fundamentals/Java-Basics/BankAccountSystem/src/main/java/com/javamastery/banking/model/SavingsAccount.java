package com.javamastery.banking.model;

import com.javamastery.banking.exception.InsufficientFundsException;
import com.javamastery.banking.exception.WithdrawalLimitExceededException;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Savings account implementation with interest earning and withdrawal restrictions.
 * Features:
 * - 2.5% annual interest rate
 * - $100 minimum balance requirement
 * - Maximum 6 withdrawals per month
 */
public class SavingsAccount extends Account {
    private static final BigDecimal INTEREST_RATE = new BigDecimal("0.025"); // 2.5%
    private static final BigDecimal MINIMUM_BALANCE = new BigDecimal("100.00");
    private static final int MAX_WITHDRAWALS_PER_MONTH = 6;
    
    private BigDecimal interestRate;
    private BigDecimal minimumBalance;
    private int withdrawalCount;
    private int maxWithdrawalsPerMonth;

    /**
     * Constructor for SavingsAccount.
     *
     * @param accountNumber    Account number
     * @param accountHolderName Account holder name
     * @param initialBalance   Initial deposit
     */
    public SavingsAccount(String accountNumber, String accountHolderName, BigDecimal initialBalance) {
        super(accountNumber, accountHolderName, initialBalance);
        this.interestRate = INTEREST_RATE;
        this.minimumBalance = MINIMUM_BALANCE;
        this.withdrawalCount = 0;
        this.maxWithdrawalsPerMonth = MAX_WITHDRAWALS_PER_MONTH;
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
        return "Savings";
    }

    /**
     * Withdraw with minimum balance and withdrawal limit restrictions.
     *
     * @param amount Amount to withdraw
     * @throws InsufficientFundsException if insufficient funds or would violate minimum balance
     * @throws WithdrawalLimitExceededException if monthly withdrawal limit exceeded
     */
    @Override
    public void withdraw(BigDecimal amount) throws InsufficientFundsException, WithdrawalLimitExceededException {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be positive");
        }

        // Check withdrawal limit
        if (withdrawalCount >= maxWithdrawalsPerMonth) {
            throw new WithdrawalLimitExceededException(
                String.format("Monthly withdrawal limit exceeded. Maximum allowed: %d", maxWithdrawalsPerMonth));
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
        withdrawalCount++;
    }

    /**
     * Applies interest to the account balance.
     */
    public void applyInterest() {
        BigDecimal interest = calculateInterest();
        setBalance(getBalance().add(interest));
    }

    /**
     * Resets the monthly withdrawal counter (typically called at the beginning of each month).
     */
    public void resetMonthlyWithdrawals() {
        this.withdrawalCount = 0;
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

    // Getters
    public BigDecimal getInterestRate() {
        return interestRate;
    }

    public BigDecimal getMinimumBalance() {
        return minimumBalance;
    }

    public int getWithdrawalCount() {
        return withdrawalCount;
    }

    public int getMaxWithdrawalsPerMonth() {
        return maxWithdrawalsPerMonth;
    }

    public int getRemainingWithdrawals() {
        return maxWithdrawalsPerMonth - withdrawalCount;
    }

    @Override
    public String toString() {
        return String.format("SavingsAccount{Number='%s', Holder='%s', Balance=%.2f, InterestRate=%.3f%%, MinBalance=%.2f, Withdrawals=%d/%d}",
                getAccountNumber(), getAccountHolderName(), getBalance(), 
                interestRate.multiply(new BigDecimal("100")), minimumBalance, withdrawalCount, maxWithdrawalsPerMonth);
    }
}