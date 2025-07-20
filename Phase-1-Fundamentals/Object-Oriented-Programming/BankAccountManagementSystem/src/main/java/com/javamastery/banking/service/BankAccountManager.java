package com.javamastery.banking.service;

import com.javamastery.banking.exception.InsufficientFundsException;
import com.javamastery.banking.exception.InvalidAccountException;
import com.javamastery.banking.exception.WithdrawalLimitExceededException;
import com.javamastery.banking.model.Account;
import com.javamastery.banking.model.BusinessAccount;
import com.javamastery.banking.model.CheckingAccount;
import com.javamastery.banking.model.SavingsAccount;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Service class for managing bank accounts and their operations.
 * Provides functionality to create accounts, perform transactions, and manage account data.
 */
public class BankAccountManager {
    
    private final List<Account> accounts;
    
    /**
     * Constructs a new BankAccountManager with an empty list of accounts.
     */
    public BankAccountManager() {
        this.accounts = new ArrayList<>();
    }
    
    /**
     * Creates a new checking account.
     * 
     * @param accountHolderName the name of the account holder
     * @param initialBalance the initial balance
     * @return the created CheckingAccount
     * @throws IllegalArgumentException if parameters are invalid
     */
    public CheckingAccount createCheckingAccount(String accountHolderName, BigDecimal initialBalance) {
        CheckingAccount account = new CheckingAccount(accountHolderName, initialBalance);
        accounts.add(account);
        System.out.println("Created checking account: " + account.getAccountNumber());
        return account;
    }
    
    /**
     * Creates a new savings account.
     * 
     * @param accountHolderName the name of the account holder
     * @param initialBalance the initial balance
     * @return the created SavingsAccount
     * @throws IllegalArgumentException if parameters are invalid
     */
    public SavingsAccount createSavingsAccount(String accountHolderName, BigDecimal initialBalance) {
        SavingsAccount account = new SavingsAccount(accountHolderName, initialBalance);
        accounts.add(account);
        System.out.println("Created savings account: " + account.getAccountNumber());
        return account;
    }
    
    /**
     * Creates a new business account.
     * 
     * @param accountHolderName the name of the account holder
     * @param businessName the name of the business
     * @param taxId the business tax ID
     * @param initialBalance the initial balance
     * @return the created BusinessAccount
     * @throws IllegalArgumentException if parameters are invalid
     */
    public BusinessAccount createBusinessAccount(String accountHolderName, String businessName, 
                                               String taxId, BigDecimal initialBalance) {
        BusinessAccount account = new BusinessAccount(accountHolderName, businessName, taxId, initialBalance);
        accounts.add(account);
        System.out.println("Created business account: " + account.getAccountNumber());
        return account;
    }
    
    /**
     * Finds an account by account number.
     * 
     * @param accountNumber the account number to search for
     * @return Optional containing the account if found, empty otherwise
     */
    public Optional<Account> findAccount(String accountNumber) {
        return accounts.stream()
                .filter(account -> account.getAccountNumber().equals(accountNumber))
                .findFirst();
    }
    
    /**
     * Finds accounts by account holder name.
     * 
     * @param accountHolderName the account holder name to search for
     * @return list of accounts belonging to the account holder
     */
    public List<Account> findAccountsByHolder(String accountHolderName) {
        return accounts.stream()
                .filter(account -> account.getAccountHolderName().equalsIgnoreCase(accountHolderName))
                .toList();
    }
    
    /**
     * Deposits money to an account.
     * 
     * @param accountNumber the account number
     * @param amount the amount to deposit
     * @throws InvalidAccountException if account is not found
     * @throws IllegalArgumentException if amount is invalid
     */
    public void deposit(String accountNumber, BigDecimal amount) throws InvalidAccountException {
        Account account = findAccount(accountNumber)
                .orElseThrow(() -> new InvalidAccountException("Account not found: " + accountNumber));
        
        account.deposit(amount);
    }
    
    /**
     * Withdraws money from an account.
     * 
     * @param accountNumber the account number
     * @param amount the amount to withdraw
     * @throws InvalidAccountException if account is not found
     * @throws InsufficientFundsException if insufficient funds
     * @throws WithdrawalLimitExceededException if withdrawal limits exceeded
     */
    public void withdraw(String accountNumber, BigDecimal amount) 
            throws InvalidAccountException, InsufficientFundsException, WithdrawalLimitExceededException {
        Account account = findAccount(accountNumber)
                .orElseThrow(() -> new InvalidAccountException("Account not found: " + accountNumber));
        
        account.withdraw(amount);
    }
    
    /**
     * Transfers money between accounts.
     * 
     * @param fromAccountNumber the source account number
     * @param toAccountNumber the destination account number
     * @param amount the amount to transfer
     * @throws InvalidAccountException if either account is not found
     * @throws InsufficientFundsException if insufficient funds in source account
     * @throws WithdrawalLimitExceededException if withdrawal limits exceeded
     */
    public void transfer(String fromAccountNumber, String toAccountNumber, BigDecimal amount)
            throws InvalidAccountException, InsufficientFundsException, WithdrawalLimitExceededException {
        
        Account fromAccount = findAccount(fromAccountNumber)
                .orElseThrow(() -> new InvalidAccountException("Source account not found: " + fromAccountNumber));
        
        Account toAccount = findAccount(toAccountNumber)
                .orElseThrow(() -> new InvalidAccountException("Destination account not found: " + toAccountNumber));
        
        // For now, only allow transfers between accounts of the same holder
        if (!fromAccount.getAccountHolderName().equalsIgnoreCase(toAccount.getAccountHolderName())) {
            throw new InvalidAccountException("Transfers are only allowed between accounts of the same holder");
        }
        
        // Perform the transfer
        fromAccount.withdraw(amount);
        toAccount.deposit(amount);
        
        System.out.println("Transferred $" + amount + " from account " + fromAccountNumber + 
                          " to account " + toAccountNumber);
    }
    
    /**
     * Gets the balance of an account.
     * 
     * @param accountNumber the account number
     * @return the account balance
     * @throws InvalidAccountException if account is not found
     */
    public BigDecimal getBalance(String accountNumber) throws InvalidAccountException {
        Account account = findAccount(accountNumber)
                .orElseThrow(() -> new InvalidAccountException("Account not found: " + accountNumber));
        
        return account.getBalance();
    }
    
    /**
     * Calculates interest for all eligible accounts.
     */
    public void calculateInterestForAllAccounts() {
        System.out.println("\n=== Calculating Interest for All Accounts ===");
        for (Account account : accounts) {
            BigDecimal interest = account.calculateInterest();
            if (interest.compareTo(BigDecimal.ZERO) > 0) {
                System.out.println("Account " + account.getAccountNumber() + " (" + 
                                 account.getAccountType() + "): Interest = $" + interest);
                
                // Apply interest for savings and business accounts
                if (account instanceof SavingsAccount) {
                    ((SavingsAccount) account).applyInterest();
                } else if (account instanceof BusinessAccount) {
                    ((BusinessAccount) account).applyInterest();
                }
            } else {
                System.out.println("Account " + account.getAccountNumber() + " (" + 
                                 account.getAccountType() + "): No interest applicable");
            }
        }
    }
    
    /**
     * Applies monthly fees to all checking accounts.
     */
    public void applyMonthlyFees() {
        System.out.println("\n=== Applying Monthly Fees ===");
        for (Account account : accounts) {
            if (account instanceof CheckingAccount) {
                try {
                    ((CheckingAccount) account).applyMonthlyFee();
                } catch (InsufficientFundsException e) {
                    System.out.println("Warning: Could not apply fee to account " + 
                                     account.getAccountNumber() + " - " + e.getMessage());
                }
            }
        }
    }
    
    /**
     * Resets monthly withdrawal counters for savings accounts.
     */
    public void resetMonthlyWithdrawals() {
        System.out.println("\n=== Resetting Monthly Withdrawal Limits ===");
        for (Account account : accounts) {
            if (account instanceof SavingsAccount) {
                ((SavingsAccount) account).resetMonthlyWithdrawals();
            }
        }
    }
    
    /**
     * Returns a list of all accounts.
     * 
     * @return an unmodifiable view of all accounts
     */
    public List<Account> getAllAccounts() {
        return List.copyOf(accounts);
    }
    
    /**
     * Returns the total number of accounts.
     * 
     * @return the number of accounts
     */
    public int getAccountCount() {
        return accounts.size();
    }
    
    /**
     * Returns the total balance across all accounts.
     * 
     * @return the total balance
     */
    public BigDecimal getTotalBalance() {
        return accounts.stream()
                .map(Account::getBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    /**
     * Displays a summary of all accounts.
     */
    public void displayAccountSummary() {
        System.out.println("\n=== Account Summary ===");
        System.out.println("Total Accounts: " + getAccountCount());
        System.out.println("Total Balance: $" + getTotalBalance());
        System.out.println("\nAccount Details:");
        
        if (accounts.isEmpty()) {
            System.out.println("No accounts found.");
        } else {
            for (Account account : accounts) {
                System.out.println(account);
                
                // Display additional information for specific account types
                if (account instanceof CheckingAccount) {
                    CheckingAccount checking = (CheckingAccount) account;
                    System.out.println("  - Available Funds: $" + checking.getAvailableFunds());
                    System.out.println("  - Overdrawn: " + (checking.isOverdrawn() ? "Yes" : "No"));
                } else if (account instanceof SavingsAccount) {
                    SavingsAccount savings = (SavingsAccount) account;
                    System.out.println("  - Remaining Withdrawals: " + savings.getRemainingWithdrawals());
                    System.out.println("  - Interest Rate: " + (savings.getAnnualInterestRate().multiply(new BigDecimal("100"))) + "%");
                } else if (account instanceof BusinessAccount) {
                    BusinessAccount business = (BusinessAccount) account;
                    System.out.println("  - Business: " + business.getBusinessName());
                    System.out.println("  - Tax ID: " + business.getTaxId());
                    System.out.println("  - Interest Rate: " + (business.getAnnualInterestRate().multiply(new BigDecimal("100"))) + "%");
                }
                System.out.println();
            }
        }
    }
}