package com.javamastery.banking.model;

import com.javamastery.banking.exception.InsufficientFundsException;
import com.javamastery.banking.exception.WithdrawalLimitExceededException;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * A checking account with overdraft protection and monthly fees.
 * Allows withdrawals up to the overdraft limit beyond the account balance.
 */
public class CheckingAccount extends Account {
    
    private static final BigDecimal OVERDRAFT_LIMIT = new BigDecimal("500.00");
    private static final BigDecimal MONTHLY_FEE = new BigDecimal("10.00");
    
    /**
     * Constructs a new CheckingAccount with the specified account holder name and initial balance.
     * 
     * @param accountHolderName the name of the account holder
     * @param initialBalance the initial balance (must be non-negative)
     * @throws IllegalArgumentException if accountHolderName is null/empty or initialBalance is negative
     */
    public CheckingAccount(String accountHolderName, BigDecimal initialBalance) {
        super(accountHolderName, initialBalance);
    }
    
    /**
     * Withdraws the specified amount from this checking account.
     * Allows overdraft up to the overdraft limit.
     * 
     * @param amount the amount to withdraw (must be positive)
     * @throws IllegalArgumentException if amount is null or not positive
     * @throws InsufficientFundsException if withdrawal exceeds balance plus overdraft limit
     */
    @Override
    public void withdraw(BigDecimal amount) throws InsufficientFundsException, WithdrawalLimitExceededException {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be positive");
        }
        
        BigDecimal availableFunds = balance.add(OVERDRAFT_LIMIT);
        if (amount.compareTo(availableFunds) > 0) {
            throw new InsufficientFundsException(
                String.format("Insufficient funds. Available (including overdraft): $%s, Attempted withdrawal: $%s",
                    availableFunds, amount));
        }
        
        this.balance = this.balance.subtract(amount.setScale(2, RoundingMode.HALF_UP));
        System.out.println("Withdrew $" + amount + " from checking account " + accountNumber + 
                          ". New balance: $" + balance);
        
        if (balance.compareTo(BigDecimal.ZERO) < 0) {
            System.out.println("Account is overdrawn by $" + balance.abs());
        }
    }
    
    /**
     * Applies the monthly fee to this checking account.
     * 
     * @throws InsufficientFundsException if applying the fee would exceed overdraft limit
     */
    public void applyMonthlyFee() throws InsufficientFundsException {
        BigDecimal newBalance = balance.subtract(MONTHLY_FEE);
        BigDecimal minimumAllowed = OVERDRAFT_LIMIT.negate();
        
        if (newBalance.compareTo(minimumAllowed) < 0) {
            throw new InsufficientFundsException(
                "Cannot apply monthly fee. Would exceed overdraft limit of $" + OVERDRAFT_LIMIT);
        }
        
        this.balance = newBalance;
        System.out.println("Applied monthly fee of $" + MONTHLY_FEE + " to account " + accountNumber + 
                          ". New balance: $" + balance);
    }
    
    /**
     * Checking accounts do not earn interest.
     * 
     * @return BigDecimal.ZERO
     */
    @Override
    public BigDecimal calculateInterest() {
        return BigDecimal.ZERO;
    }
    
    @Override
    public String getAccountType() {
        return "Checking Account";
    }
    
    /**
     * Returns the overdraft limit for checking accounts.
     * 
     * @return the overdraft limit
     */
    public BigDecimal getOverdraftLimit() {
        return OVERDRAFT_LIMIT;
    }
    
    /**
     * Returns the monthly fee for checking accounts.
     * 
     * @return the monthly fee
     */
    public BigDecimal getMonthlyFee() {
        return MONTHLY_FEE;
    }
    
    /**
     * Checks if the account is currently overdrawn.
     * 
     * @return true if balance is negative, false otherwise
     */
    public boolean isOverdrawn() {
        return balance.compareTo(BigDecimal.ZERO) < 0;
    }
    
    /**
     * Returns the available funds including overdraft protection.
     * 
     * @return the total available funds
     */
    public BigDecimal getAvailableFunds() {
        return balance.add(OVERDRAFT_LIMIT);
    }
}