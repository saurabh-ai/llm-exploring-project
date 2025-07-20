package com.javamastery.banking;

import com.javamastery.banking.exception.InsufficientFundsException;
import com.javamastery.banking.exception.InvalidAccountException;
import com.javamastery.banking.exception.WithdrawalLimitExceededException;
import com.javamastery.banking.model.Account;
import com.javamastery.banking.service.BankAccountManager;
import com.javamastery.banking.util.InputValidator;
import java.math.BigDecimal;
import java.util.List;

/**
 * Main application class for the Bank Account Management System.
 * Provides a console-based menu interface for managing bank accounts.
 */
public class BankingApp {
    
    private final BankAccountManager accountManager;
    
    /**
     * Constructs a new BankingApp with a new BankAccountManager.
     */
    public BankingApp() {
        this.accountManager = new BankAccountManager();
    }
    
    /**
     * Main method to start the application.
     * 
     * @param args command line arguments (not used)
     */
    public static void main(String[] args) {
        System.out.println("=== Welcome to Bank Account Management System ===");
        System.out.println("Demonstrating Java OOP Principles and Banking Operations\n");
        
        BankingApp app = new BankingApp();
        app.run();
    }
    
    /**
     * Main application loop.
     */
    public void run() {
        boolean running = true;
        
        while (running) {
            displayMainMenu();
            int choice = InputValidator.getValidInteger("Enter your choice: ", 1, 12);
            
            try {
                switch (choice) {
                    case 1 -> createAccount();
                    case 2 -> depositMoney();
                    case 3 -> withdrawMoney();
                    case 4 -> transferMoney();
                    case 5 -> checkBalance();
                    case 6 -> searchAccounts();
                    case 7 -> accountManager.displayAccountSummary();
                    case 8 -> accountManager.calculateInterestForAllAccounts();
                    case 9 -> accountManager.applyMonthlyFees();
                    case 10 -> accountManager.resetMonthlyWithdrawals();
                    case 11 -> demonstrateFeatures();
                    case 12 -> {
                        System.out.println("Thank you for using Bank Account Management System!");
                        running = false;
                    }
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
            
            if (running) {
                System.out.println("\nPress Enter to continue...");
                InputValidator.getValidString("");
            }
        }
    }
    
    /**
     * Displays the main menu options.
     */
    private void displayMainMenu() {
        System.out.println("\n=== Bank Account Management System ===");
        System.out.println("1.  Create Account");
        System.out.println("2.  Deposit Money");
        System.out.println("3.  Withdraw Money");
        System.out.println("4.  Transfer Money");
        System.out.println("5.  Check Balance");
        System.out.println("6.  Search Accounts");
        System.out.println("7.  Account Summary");
        System.out.println("8.  Calculate Interest");
        System.out.println("9.  Apply Monthly Fees");
        System.out.println("10. Reset Monthly Withdrawals");
        System.out.println("11. Demonstrate Features");
        System.out.println("12. Exit");
        System.out.println("==========================================");
    }
    
    /**
     * Handles account creation based on user selection.
     */
    private void createAccount() {
        System.out.println("\n=== Create Account ===");
        System.out.println("1. Checking Account ($500 overdraft, $10 monthly fee)");
        System.out.println("2. Savings Account ($100 minimum, 2.5% interest, 6 withdrawals/month)");
        System.out.println("3. Business Account ($10,000 minimum, 1.5% interest)");
        
        int accountType = InputValidator.getValidInteger("Select account type: ", 1, 3);
        String holderName = InputValidator.getValidString("Enter account holder name: ");
        
        try {
            switch (accountType) {
                case 1 -> {
                    BigDecimal initialBalance = InputValidator.getValidAmount("Enter initial balance: $", true);
                    accountManager.createCheckingAccount(holderName, initialBalance);
                }
                case 2 -> {
                    System.out.println("Minimum balance required: $100.00");
                    BigDecimal initialBalance = InputValidator.getValidAmount("Enter initial balance: $", false);
                    accountManager.createSavingsAccount(holderName, initialBalance);
                }
                case 3 -> {
                    String businessName = InputValidator.getValidString("Enter business name: ");
                    String taxId = InputValidator.getValidString("Enter tax ID: ");
                    System.out.println("Minimum balance required: $10,000.00");
                    BigDecimal initialBalance = InputValidator.getValidAmount("Enter initial balance: $", false);
                    accountManager.createBusinessAccount(holderName, businessName, taxId, initialBalance);
                }
            }
            System.out.println("Account created successfully!");
        } catch (IllegalArgumentException e) {
            System.out.println("Failed to create account: " + e.getMessage());
        }
    }
    
    /**
     * Handles money deposit operations.
     */
    private void depositMoney() {
        System.out.println("\n=== Deposit Money ===");
        String accountNumber = InputValidator.getValidString("Enter account number: ");
        BigDecimal amount = InputValidator.getValidAmount("Enter deposit amount: $", false);
        
        try {
            accountManager.deposit(accountNumber, amount);
            System.out.println("Deposit successful!");
        } catch (InvalidAccountException e) {
            System.out.println("Deposit failed: " + e.getMessage());
        }
    }
    
    /**
     * Handles money withdrawal operations.
     */
    private void withdrawMoney() {
        System.out.println("\n=== Withdraw Money ===");
        String accountNumber = InputValidator.getValidString("Enter account number: ");
        BigDecimal amount = InputValidator.getValidAmount("Enter withdrawal amount: $", false);
        
        try {
            accountManager.withdraw(accountNumber, amount);
            System.out.println("Withdrawal successful!");
        } catch (InvalidAccountException | InsufficientFundsException | WithdrawalLimitExceededException e) {
            System.out.println("Withdrawal failed: " + e.getMessage());
        }
    }
    
    /**
     * Handles money transfer operations.
     */
    private void transferMoney() {
        System.out.println("\n=== Transfer Money ===");
        String fromAccount = InputValidator.getValidString("Enter source account number: ");
        String toAccount = InputValidator.getValidString("Enter destination account number: ");
        BigDecimal amount = InputValidator.getValidAmount("Enter transfer amount: $", false);
        
        try {
            accountManager.transfer(fromAccount, toAccount, amount);
            System.out.println("Transfer successful!");
        } catch (InvalidAccountException | InsufficientFundsException | WithdrawalLimitExceededException e) {
            System.out.println("Transfer failed: " + e.getMessage());
        }
    }
    
    /**
     * Handles balance inquiry operations.
     */
    private void checkBalance() {
        System.out.println("\n=== Check Balance ===");
        String accountNumber = InputValidator.getValidString("Enter account number: ");
        
        try {
            BigDecimal balance = accountManager.getBalance(accountNumber);
            System.out.println("Current balance: $" + balance);
        } catch (InvalidAccountException e) {
            System.out.println("Balance inquiry failed: " + e.getMessage());
        }
    }
    
    /**
     * Handles account search operations.
     */
    private void searchAccounts() {
        System.out.println("\n=== Search Accounts ===");
        System.out.println("1. Search by Account Number");
        System.out.println("2. Search by Account Holder Name");
        
        int searchType = InputValidator.getValidInteger("Select search type: ", 1, 2);
        
        switch (searchType) {
            case 1 -> {
                String accountNumber = InputValidator.getValidString("Enter account number: ");
                var account = accountManager.findAccount(accountNumber);
                if (account.isPresent()) {
                    System.out.println("Account found:");
                    System.out.println(account.get());
                } else {
                    System.out.println("Account not found.");
                }
            }
            case 2 -> {
                String holderName = InputValidator.getValidString("Enter account holder name: ");
                List<Account> accounts = accountManager.findAccountsByHolder(holderName);
                if (accounts.isEmpty()) {
                    System.out.println("No accounts found for holder: " + holderName);
                } else {
                    System.out.println("Found " + accounts.size() + " account(s):");
                    for (Account account : accounts) {
                        System.out.println(account);
                    }
                }
            }
        }
    }
    
    /**
     * Demonstrates the key features of the banking system.
     */
    private void demonstrateFeatures() {
        System.out.println("\n=== Feature Demonstration ===");
        
        if (!InputValidator.getConfirmation("This will create sample accounts and perform transactions. Continue?")) {
            return;
        }
        
        try {
            // Create sample accounts
            System.out.println("\n1. Creating sample accounts...");
            var checking = accountManager.createCheckingAccount("John Doe", new BigDecimal("1000.00"));
            var savings = accountManager.createSavingsAccount("John Doe", new BigDecimal("5000.00"));
            var business = accountManager.createBusinessAccount("Jane Smith", "Smith Corp", "12-3456789", new BigDecimal("25000.00"));
            
            // Demonstrate deposits
            System.out.println("\n2. Demonstrating deposits...");
            accountManager.deposit(checking.getAccountNumber(), new BigDecimal("500.00"));
            accountManager.deposit(savings.getAccountNumber(), new BigDecimal("2000.00"));
            
            // Demonstrate withdrawals
            System.out.println("\n3. Demonstrating withdrawals...");
            accountManager.withdraw(checking.getAccountNumber(), new BigDecimal("1200.00")); // Test overdraft
            accountManager.withdraw(savings.getAccountNumber(), new BigDecimal("1000.00")); // Normal withdrawal
            
            // Demonstrate transfer
            System.out.println("\n4. Demonstrating transfer...");
            accountManager.transfer(savings.getAccountNumber(), checking.getAccountNumber(), new BigDecimal("500.00"));
            
            // Calculate interest
            System.out.println("\n5. Calculating interest...");
            accountManager.calculateInterestForAllAccounts();
            
            // Apply monthly fee
            System.out.println("\n6. Applying monthly fees...");
            accountManager.applyMonthlyFees();
            
            // Show final summary
            System.out.println("\n7. Final account summary:");
            accountManager.displayAccountSummary();
            
            System.out.println("\nDemonstration completed successfully!");
            
        } catch (Exception e) {
            System.out.println("Demonstration error: " + e.getMessage());
        }
    }
}