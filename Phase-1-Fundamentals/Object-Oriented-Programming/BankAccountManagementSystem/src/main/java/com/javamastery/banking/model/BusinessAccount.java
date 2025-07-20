package com.javamastery.banking.model;

import com.javamastery.banking.exception.InsufficientFundsException;
import com.javamastery.banking.exception.WithdrawalLimitExceededException;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * A business account with higher minimum balance requirements and interest earnings.
 * Designed for business customers with higher transaction volumes.
 */
public class BusinessAccount extends Account {
    
    private static final BigDecimal MINIMUM_BALANCE = new BigDecimal("10000.00");
    private static final BigDecimal ANNUAL_INTEREST_RATE = new BigDecimal("0.015"); // 1.5%
    
    private final String businessName;
    private final String taxId;
    
    /**
     * Constructs a new BusinessAccount with the specified details.
     * 
     * @param accountHolderName the name of the account holder
     * @param businessName the name of the business
     * @param taxId the business tax identification number
     * @param initialBalance the initial balance (must meet minimum balance requirement)
     * @throws IllegalArgumentException if any parameter is null/empty or initialBalance is below minimum
     */
    public BusinessAccount(String accountHolderName, String businessName, String taxId, BigDecimal initialBalance) {
        super(accountHolderName, initialBalance);
        
        if (businessName == null || businessName.trim().isEmpty()) {
            throw new IllegalArgumentException("Business name cannot be null or empty");
        }
        if (taxId == null || taxId.trim().isEmpty()) {
            throw new IllegalArgumentException("Tax ID cannot be null or empty");
        }
        if (initialBalance.compareTo(MINIMUM_BALANCE) < 0) {
            throw new IllegalArgumentException(
                "Initial balance must be at least $" + MINIMUM_BALANCE + " for business account");
        }
        
        this.businessName = businessName.trim();
        this.taxId = taxId.trim();
    }
    
    /**
     * Withdraws the specified amount from this business account.
     * Enforces minimum balance requirement (no overdraft allowed).
     * 
     * @param amount the amount to withdraw (must be positive)
     * @throws IllegalArgumentException if amount is null or not positive
     * @throws InsufficientFundsException if withdrawal would violate minimum balance
     */
    @Override
    public void withdraw(BigDecimal amount) throws InsufficientFundsException, WithdrawalLimitExceededException {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be positive");
        }
        
        BigDecimal newBalance = balance.subtract(amount);
        if (newBalance.compareTo(MINIMUM_BALANCE) < 0) {
            throw new InsufficientFundsException(
                String.format("Withdrawal would violate minimum balance requirement. " +
                    "Balance after withdrawal: $%s, Minimum required: $%s",
                    newBalance, MINIMUM_BALANCE));
        }
        
        this.balance = newBalance.setScale(2, RoundingMode.HALF_UP);
        System.out.println("Withdrew $" + amount + " from business account " + accountNumber + 
                          " (" + businessName + "). New balance: $" + balance);
    }
    
    /**
     * Calculates the annual interest for this business account.
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
            System.out.println("Applied interest of $" + interest + " to business account " + accountNumber + 
                              " (" + businessName + "). New balance: $" + balance);
        }
    }
    
    @Override
    public String getAccountType() {
        return "Business Account";
    }
    
    /**
     * Returns the minimum balance requirement for business accounts.
     * 
     * @return the minimum balance
     */
    public BigDecimal getMinimumBalance() {
        return MINIMUM_BALANCE;
    }
    
    /**
     * Returns the annual interest rate for business accounts.
     * 
     * @return the annual interest rate as a decimal
     */
    public BigDecimal getAnnualInterestRate() {
        return ANNUAL_INTEREST_RATE;
    }
    
    /**
     * Returns the business name associated with this account.
     * 
     * @return the business name
     */
    public String getBusinessName() {
        return businessName;
    }
    
    /**
     * Returns the tax ID associated with this account.
     * 
     * @return the tax ID
     */
    public String getTaxId() {
        return taxId;
    }
    
    @Override
    public String toString() {
        return String.format("%s [Account: %s, Holder: %s, Business: %s, Tax ID: %s, Balance: $%s, Created: %s]",
                getAccountType(), accountNumber, accountHolderName, businessName, taxId, balance, 
                createdDate.toLocalDate());
    }
}