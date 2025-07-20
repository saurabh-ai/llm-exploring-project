package com.javamastery.banking.service;

import com.javamastery.banking.exception.InsufficientFundsException;
import com.javamastery.banking.exception.InvalidAccountException;
import com.javamastery.banking.exception.WithdrawalLimitExceededException;
import com.javamastery.banking.model.Account;
import com.javamastery.banking.model.BusinessAccount;
import com.javamastery.banking.model.CheckingAccount;
import com.javamastery.banking.model.SavingsAccount;
import com.javamastery.banking.util.InputValidator;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class for managing bank accounts and banking operations.
 * Handles account creation, transactions, and account management.
 */
public class BankAccountManager {
    private List<Account> accounts;
    private static int accountCounter = 10000000; // Starting 8-digit account number

    /**
     * Constructor initializes the account list.
     */
    public BankAccountManager() {
        this.accounts = new ArrayList<>();
    }

    /**
     * Creates a new bank account based on the specified type.
     *
     * @param holderName     Name of the account holder
     * @param accountType    Type of account (checking, savings, business)
     * @param initialDeposit Initial deposit amount
     * @param businessName   Business name (required for business accounts)
     * @param taxId         Tax ID (required for business accounts)
     * @return Account number of the created account
     * @throws IllegalArgumentException if invalid parameters
     */
    public String createAccount(String holderName, String accountType, BigDecimal initialDeposit,
                              String businessName, String taxId) {
        // Validate inputs
        if (!InputValidator.validateName(holderName)) {
            throw new IllegalArgumentException("Invalid account holder name");
        }
        if (!InputValidator.validateAccountType(accountType)) {
            throw new IllegalArgumentException("Invalid account type. Use: checking, savings, or business");
        }
        if (!InputValidator.validateInitialDeposit(accountType, initialDeposit)) {
            BigDecimal minDeposit = InputValidator.getMinimumDeposit(accountType);
            throw new IllegalArgumentException(
                String.format("Initial deposit of %.2f does not meet minimum requirement of %.2f for %s account",
                    initialDeposit, minDeposit, accountType));
        }

        // Generate account number
        String accountNumber = String.valueOf(accountCounter++);
        String formattedName = InputValidator.formatName(holderName);
        String normalizedType = InputValidator.normalizeAccountType(accountType);

        // Create appropriate account type
        Account account;
        switch (normalizedType) {
            case "Checking":
                account = new CheckingAccount(accountNumber, formattedName, initialDeposit);
                break;
            case "Savings":
                account = new SavingsAccount(accountNumber, formattedName, initialDeposit);
                break;
            case "Business":
                if (!InputValidator.validateBusinessName(businessName)) {
                    throw new IllegalArgumentException("Invalid business name");
                }
                if (!InputValidator.validateTaxId(taxId)) {
                    throw new IllegalArgumentException("Invalid tax ID format. Use: XX-XXXXXXX");
                }
                account = new BusinessAccount(accountNumber, formattedName, businessName, taxId, initialDeposit);
                break;
            default:
                throw new IllegalArgumentException("Unsupported account type: " + accountType);
        }

        accounts.add(account);
        return accountNumber;
    }

    /**
     * Overloaded method for creating checking and savings accounts (no business info required).
     */
    public String createAccount(String holderName, String accountType, BigDecimal initialDeposit) {
        return createAccount(holderName, accountType, initialDeposit, null, null);
    }

    /**
     * Deposits money into the specified account.
     *
     * @param accountNumber Account number
     * @param amount       Amount to deposit
     * @throws InvalidAccountException if account not found
     * @throws IllegalArgumentException if invalid amount
     */
    public void deposit(String accountNumber, BigDecimal amount) throws InvalidAccountException {
        if (!InputValidator.validateAmount(amount)) {
            throw new IllegalArgumentException("Invalid deposit amount");
        }

        Account account = findAccount(accountNumber);
        account.deposit(amount);
    }

    /**
     * Withdraws money from the specified account.
     *
     * @param accountNumber Account number
     * @param amount       Amount to withdraw
     * @throws InvalidAccountException if account not found
     * @throws InsufficientFundsException if insufficient funds
     * @throws WithdrawalLimitExceededException if withdrawal limit exceeded
     */
    public void withdraw(String accountNumber, BigDecimal amount) 
            throws InvalidAccountException, InsufficientFundsException, WithdrawalLimitExceededException {
        if (!InputValidator.validateAmount(amount)) {
            throw new IllegalArgumentException("Invalid withdrawal amount");
        }

        Account account = findAccount(accountNumber);
        try {
            account.withdraw(amount);
        } catch (InsufficientFundsException | WithdrawalLimitExceededException e) {
            throw e; // Re-throw banking exceptions
        } catch (Exception e) {
            throw new RuntimeException("Unexpected error during withdrawal: " + e.getMessage(), e);
        }
    }

    /**
     * Transfers money between two accounts.
     *
     * @param fromAccountNumber Source account number
     * @param toAccountNumber   Destination account number
     * @param amount           Amount to transfer
     * @throws InvalidAccountException if either account not found
     * @throws InsufficientFundsException if insufficient funds in source account
     * @throws WithdrawalLimitExceededException if withdrawal limit exceeded in source account
     */
    public void transfer(String fromAccountNumber, String toAccountNumber, BigDecimal amount)
            throws InvalidAccountException, InsufficientFundsException, WithdrawalLimitExceededException {
        if (!InputValidator.validateAmount(amount)) {
            throw new IllegalArgumentException("Invalid transfer amount");
        }
        if (fromAccountNumber.equals(toAccountNumber)) {
            throw new IllegalArgumentException("Cannot transfer to the same account");
        }

        Account fromAccount = findAccount(fromAccountNumber);
        Account toAccount = findAccount(toAccountNumber);

        // Perform the transfer (withdraw from source, deposit to destination)
        try {
            fromAccount.withdraw(amount);
            toAccount.deposit(amount);
        } catch (Exception e) {
            // If deposit fails after successful withdrawal, we need to restore the balance
            // This is a simplified approach - in production, you'd want proper transaction management
            throw e;
        }
    }

    /**
     * Finds an account by account number.
     *
     * @param accountNumber Account number to search for
     * @return Account object
     * @throws InvalidAccountException if account not found
     */
    public Account findAccount(String accountNumber) throws InvalidAccountException {
        if (!InputValidator.validateAccountNumber(accountNumber)) {
            throw new InvalidAccountException("Invalid account number format");
        }

        return accounts.stream()
                .filter(account -> account.getAccountNumber().equals(accountNumber))
                .findFirst()
                .orElseThrow(() -> new InvalidAccountException("Account not found: " + accountNumber));
    }

    /**
     * Gets all accounts in the system.
     *
     * @return List of all accounts
     */
    public List<Account> getAllAccounts() {
        return new ArrayList<>(accounts);
    }

    /**
     * Gets accounts by type.
     *
     * @param accountType Type of accounts to retrieve
     * @return List of accounts of the specified type
     */
    public List<Account> getAccountsByType(String accountType) {
        String normalizedType = InputValidator.normalizeAccountType(accountType);
        if (normalizedType == null) {
            return new ArrayList<>();
        }

        return accounts.stream()
                .filter(account -> account.getAccountType().equals(normalizedType))
                .collect(Collectors.toList());
    }

    /**
     * Gets accounts by holder name.
     *
     * @param holderName Name of the account holder
     * @return List of accounts for the specified holder
     */
    public List<Account> getAccountsByHolder(String holderName) {
        if (holderName == null || holderName.trim().isEmpty()) {
            return new ArrayList<>();
        }

        String formattedName = InputValidator.formatName(holderName);
        return accounts.stream()
                .filter(account -> account.getAccountHolderName().equalsIgnoreCase(formattedName))
                .collect(Collectors.toList());
    }

    /**
     * Calculates total balance across all accounts.
     *
     * @return Total balance
     */
    public BigDecimal getTotalBalance() {
        return accounts.stream()
                .map(Account::getBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Gets the count of accounts by type.
     *
     * @return String representation of account counts
     */
    public String getAccountStatistics() {
        long checkingCount = getAccountsByType("checking").size();
        long savingsCount = getAccountsByType("savings").size();
        long businessCount = getAccountsByType("business").size();
        
        return String.format("Accounts - Checking: %d, Savings: %d, Business: %d, Total: %d",
                checkingCount, savingsCount, businessCount, accounts.size());
    }

    /**
     * Gets the next account number that would be assigned.
     *
     * @return Next account number
     */
    public String getNextAccountNumber() {
        return String.valueOf(accountCounter);
    }
}