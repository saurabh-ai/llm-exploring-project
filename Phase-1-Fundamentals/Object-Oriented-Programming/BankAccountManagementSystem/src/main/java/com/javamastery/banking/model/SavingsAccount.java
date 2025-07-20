package com.javamastery.banking.model;

import com.javamastery.banking.exception.InsufficientFundsException;
import com.javamastery.banking.exception.WithdrawalLimitExceededException;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * A savings account with minimum balance requirements, interest earnings, and withdrawal limits.
 * Has a minimum balance requirement and limited monthly withdrawals.
 */
public class SavingsAccount extends Account {
    
    private static final BigDecimal MINIMUM_BALANCE = new BigDecimal("100.00");
    private static final BigDecimal ANNUAL_INTEREST_RATE = new BigDecimal("0.025"); // 2.5%
    private static final int MAX_MONTHLY_WITHDRAWALS = 6;
    
    private int monthlyWithdrawals;
    
    /**
     * Constructs a new SavingsAccount with the specified account holder name and initial balance.
     * 
     * @param accountHolderName the name of the account holder
     * @param initialBalance the initial balance (must meet minimum balance requirement)
     * @throws IllegalArgumentException if accountHolderName is null/empty or initialBalance is below minimum
     */
    public SavingsAccount(String accountHolderName, BigDecimal initialBalance) {
        super(accountHolderName, initialBalance);
        
        if (initialBalance.compareTo(MINIMUM_BALANCE) < 0) {
            throw new IllegalArgumentException(
                "Initial balance must be at least $" + MINIMUM_BALANCE + " for savings account");
        }
        
        this.monthlyWithdrawals = 0;
    }
    
    /**
     * Withdraws the specified amount from this savings account.
     * Enforces minimum balance and monthly withdrawal limits.
     * 
     * @param amount the amount to withdraw (must be positive)
     * @throws IllegalArgumentException if amount is null or not positive
     * @throws InsufficientFundsException if withdrawal would violate minimum balance
     * @throws WithdrawalLimitExceededException if monthly withdrawal limit exceeded
     */
    @Override
    public void withdraw(BigDecimal amount) throws InsufficientFundsException, WithdrawalLimitExceededException {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be positive");
        }
        
        if (monthlyWithdrawals >= MAX_MONTHLY_WITHDRAWALS) {
            throw new WithdrawalLimitExceededException(
                "Monthly withdrawal limit of " + MAX_MONTHLY_WITHDRAWALS + " exceeded");
        }
        
        BigDecimal newBalance = balance.subtract(amount);
        if (newBalance.compareTo(MINIMUM_BALANCE) < 0) {
            throw new InsufficientFundsException(
                String.format("Withdrawal would violate minimum balance requirement. " +
                    "Balance after withdrawal: $%s, Minimum required: $%s",
                    newBalance, MINIMUM_BALANCE));
        }
        
        this.balance = newBalance.setScale(2, RoundingMode.HALF_UP);
        this.monthlyWithdrawals++;
        
        System.out.println("Withdrew $" + amount + " from savings account " + accountNumber + 
                          ". New balance: $" + balance + 
                          ". Monthly withdrawals: " + monthlyWithdrawals + "/" + MAX_MONTHLY_WITHDRAWALS);
    }
    
    /**
     * Calculates the annual interest for this savings account.
     * 
     * @return the calculated annual interest amount
     */
    @Override
    public BigDecimal calculateInterest() {
        return balance.multiply(ANNUAL_INTEREST_RATE).setScale(2, RoundingMode.HALF_UP);
    }
    
    /**
     * Applies interest to the account balance.
     */
    public void applyInterest() {
        BigDecimal interest = calculateInterest();
        if (interest.compareTo(BigDecimal.ZERO) > 0) {
            this.balance = this.balance.add(interest);
            System.out.println("Applied interest of $" + interest + " to savings account " + accountNumber + 
                              ". New balance: $" + balance);
        }
    }
    
    /**
     * Resets the monthly withdrawal counter. Should be called at the beginning of each month.
     */
    public void resetMonthlyWithdrawals() {
        this.monthlyWithdrawals = 0;
        System.out.println("Reset monthly withdrawals for account " + accountNumber);
    }
    
    @Override
    public String getAccountType() {
        return "Savings Account";
    }
    
    /**
     * Returns the minimum balance requirement for savings accounts.
     * 
     * @return the minimum balance
     */
    public BigDecimal getMinimumBalance() {
        return MINIMUM_BALANCE;
    }
    
    /**
     * Returns the annual interest rate for savings accounts.
     * 
     * @return the annual interest rate as a decimal
     */
    public BigDecimal getAnnualInterestRate() {
        return ANNUAL_INTEREST_RATE;
    }
    
    /**
     * Returns the maximum number of withdrawals allowed per month.
     * 
     * @return the maximum monthly withdrawals
     */
    public int getMaxMonthlyWithdrawals() {
        return MAX_MONTHLY_WITHDRAWALS;
    }
    
    /**
     * Returns the current number of withdrawals this month.
     * 
     * @return the current monthly withdrawals count
     */
    public int getCurrentMonthlyWithdrawals() {
        return monthlyWithdrawals;
    }
    
    /**
     * Returns the remaining withdrawals allowed this month.
     * 
     * @return the remaining withdrawals
     */
    public int getRemainingWithdrawals() {
        return MAX_MONTHLY_WITHDRAWALS - monthlyWithdrawals;
    }
}