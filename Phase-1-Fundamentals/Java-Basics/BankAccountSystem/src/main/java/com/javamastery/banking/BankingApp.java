package com.javamastery.banking;

import com.javamastery.banking.exception.InsufficientFundsException;
import com.javamastery.banking.exception.InvalidAccountException;
import com.javamastery.banking.exception.WithdrawalLimitExceededException;
import com.javamastery.banking.model.Account;
import com.javamastery.banking.model.BusinessAccount;
import com.javamastery.banking.service.BankAccountManager;
import com.javamastery.banking.util.InputValidator;

import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;

/**
 * Main banking application with interactive console menu.
 * Demonstrates the bank account management system with all features.
 */
public class BankingApp {
    private static BankAccountManager bankManager = new BankAccountManager();
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println("    Welcome to Java Mastery Banking System");
        System.out.println("=================================================");
        
        // Add some sample data for demonstration
        createSampleAccounts();
        
        showMainMenu();
        
        System.out.println("\nThank you for using Java Mastery Banking System!");
        scanner.close();
    }

    /**
     * Creates sample accounts for demonstration purposes.
     */
    private static void createSampleAccounts() {
        try {
            bankManager.createAccount("John Doe", "checking", new BigDecimal("1000.00"));
            bankManager.createAccount("Jane Smith", "savings", new BigDecimal("5000.00"));
            bankManager.createAccount("Bob Wilson", "business", new BigDecimal("15000.00"), 
                                    "Wilson Consulting LLC", "12-3456789");
            System.out.println("Sample accounts created for demonstration.");
        } catch (Exception e) {
            System.out.println("Note: Could not create sample accounts: " + e.getMessage());
        }
    }

    /**
     * Displays the main menu and handles user choices.
     */
    private static void showMainMenu() {
        while (true) {
            System.out.println("\n=== MAIN MENU ===");
            System.out.println("1. Create Account");
            System.out.println("2. Deposit Money");
            System.out.println("3. Withdraw Money");
            System.out.println("4. Transfer Money");
            System.out.println("5. Check Balance");
            System.out.println("6. List All Accounts");
            System.out.println("7. Calculate Interest");
            System.out.println("8. Account Statistics");
            System.out.println("9. Exit");
            System.out.print("\nEnter your choice (1-9): ");

            try {
                int choice = Integer.parseInt(scanner.nextLine().trim());
                
                switch (choice) {
                    case 1:
                        createAccountMenu();
                        break;
                    case 2:
                        depositMenu();
                        break;
                    case 3:
                        withdrawMenu();
                        break;
                    case 4:
                        transferMenu();
                        break;
                    case 5:
                        checkBalanceMenu();
                        break;
                    case 6:
                        listAllAccountsMenu();
                        break;
                    case 7:
                        calculateInterestMenu();
                        break;
                    case 8:
                        showStatistics();
                        break;
                    case 9:
                        return;
                    default:
                        System.out.println("Invalid choice. Please enter a number between 1 and 9.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
            }
        }
    }

    /**
     * Handles account creation menu.
     */
    private static void createAccountMenu() {
        System.out.println("\n=== CREATE ACCOUNT ===");
        
        try {
            System.out.print("Enter account holder name: ");
            String holderName = scanner.nextLine().trim();
            
            System.out.println("Select account type:");
            System.out.println("1. Checking Account (No minimum balance)");
            System.out.println("2. Savings Account (Minimum $100)");
            System.out.println("3. Business Account (Minimum $10,000)");
            System.out.print("Enter choice (1-3): ");
            
            int typeChoice = Integer.parseInt(scanner.nextLine().trim());
            String accountType;
            
            switch (typeChoice) {
                case 1:
                    accountType = "checking";
                    break;
                case 2:
                    accountType = "savings";
                    break;
                case 3:
                    accountType = "business";
                    break;
                default:
                    System.out.println("Invalid account type choice.");
                    return;
            }
            
            System.out.print("Enter initial deposit amount: $");
            BigDecimal initialDeposit = new BigDecimal(scanner.nextLine().trim());
            
            String businessName = null;
            String taxId = null;
            
            if ("business".equals(accountType)) {
                System.out.print("Enter business name: ");
                businessName = scanner.nextLine().trim();
                System.out.print("Enter tax ID (format XX-XXXXXXX): ");
                taxId = scanner.nextLine().trim();
            }
            
            String accountNumber = bankManager.createAccount(holderName, accountType, initialDeposit, businessName, taxId);
            System.out.println("Account created successfully!");
            System.out.println("Account Number: " + accountNumber);
            System.out.println("Account Type: " + InputValidator.normalizeAccountType(accountType));
            System.out.println("Initial Deposit: $" + initialDeposit);
            
        } catch (Exception e) {
            System.out.println("Error creating account: " + e.getMessage());
        }
    }

    /**
     * Handles deposit menu.
     */
    private static void depositMenu() {
        System.out.println("\n=== DEPOSIT MONEY ===");
        
        try {
            System.out.print("Enter account number: ");
            String accountNumber = scanner.nextLine().trim();
            
            System.out.print("Enter deposit amount: $");
            BigDecimal amount = new BigDecimal(scanner.nextLine().trim());
            
            BigDecimal balanceBefore = bankManager.findAccount(accountNumber).getBalance();
            bankManager.deposit(accountNumber, amount);
            BigDecimal balanceAfter = bankManager.findAccount(accountNumber).getBalance();
            
            System.out.println("Deposit successful!");
            System.out.println("Amount deposited: $" + amount);
            System.out.println("Balance before: $" + balanceBefore);
            System.out.println("Balance after: $" + balanceAfter);
            
        } catch (Exception e) {
            System.out.println("Error processing deposit: " + e.getMessage());
        }
    }

    /**
     * Handles withdrawal menu.
     */
    private static void withdrawMenu() {
        System.out.println("\n=== WITHDRAW MONEY ===");
        
        try {
            System.out.print("Enter account number: ");
            String accountNumber = scanner.nextLine().trim();
            
            Account account = bankManager.findAccount(accountNumber);
            System.out.println("Current balance: $" + account.getBalance());
            
            System.out.print("Enter withdrawal amount: $");
            BigDecimal amount = new BigDecimal(scanner.nextLine().trim());
            
            BigDecimal balanceBefore = account.getBalance();
            bankManager.withdraw(accountNumber, amount);
            BigDecimal balanceAfter = bankManager.findAccount(accountNumber).getBalance();
            
            System.out.println("Withdrawal successful!");
            System.out.println("Amount withdrawn: $" + amount);
            System.out.println("Balance before: $" + balanceBefore);
            System.out.println("Balance after: $" + balanceAfter);
            
        } catch (InsufficientFundsException | WithdrawalLimitExceededException e) {
            System.out.println("Withdrawal failed: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error processing withdrawal: " + e.getMessage());
        }
    }

    /**
     * Handles transfer menu.
     */
    private static void transferMenu() {
        System.out.println("\n=== TRANSFER MONEY ===");
        
        try {
            System.out.print("Enter source account number: ");
            String fromAccount = scanner.nextLine().trim();
            
            System.out.print("Enter destination account number: ");
            String toAccount = scanner.nextLine().trim();
            
            Account sourceAccount = bankManager.findAccount(fromAccount);
            Account destAccount = bankManager.findAccount(toAccount);
            
            System.out.println("Source account balance: $" + sourceAccount.getBalance());
            System.out.println("Destination account balance: $" + destAccount.getBalance());
            
            System.out.print("Enter transfer amount: $");
            BigDecimal amount = new BigDecimal(scanner.nextLine().trim());
            
            bankManager.transfer(fromAccount, toAccount, amount);
            
            System.out.println("Transfer successful!");
            System.out.println("Amount transferred: $" + amount);
            System.out.println("From account (" + fromAccount + ") new balance: $" + 
                             bankManager.findAccount(fromAccount).getBalance());
            System.out.println("To account (" + toAccount + ") new balance: $" + 
                             bankManager.findAccount(toAccount).getBalance());
            
        } catch (InsufficientFundsException | WithdrawalLimitExceededException e) {
            System.out.println("Transfer failed: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error processing transfer: " + e.getMessage());
        }
    }

    /**
     * Handles balance inquiry menu.
     */
    private static void checkBalanceMenu() {
        System.out.println("\n=== CHECK BALANCE ===");
        
        try {
            System.out.print("Enter account number: ");
            String accountNumber = scanner.nextLine().trim();
            
            Account account = bankManager.findAccount(accountNumber);
            System.out.println("\nAccount Information:");
            System.out.println("Account Number: " + account.getAccountNumber());
            System.out.println("Account Holder: " + account.getAccountHolderName());
            System.out.println("Account Type: " + account.getAccountType());
            System.out.println("Current Balance: $" + account.getBalance());
            System.out.println("Created Date: " + account.getCreatedDate().toLocalDate());
            
        } catch (Exception e) {
            System.out.println("Error retrieving account information: " + e.getMessage());
        }
    }

    /**
     * Lists all accounts.
     */
    private static void listAllAccountsMenu() {
        System.out.println("\n=== ALL ACCOUNTS ===");
        
        List<Account> accounts = bankManager.getAllAccounts();
        
        if (accounts.isEmpty()) {
            System.out.println("No accounts found.");
            return;
        }
        
        System.out.println("Total accounts: " + accounts.size());
        System.out.println("-".repeat(100));
        
        for (Account account : accounts) {
            System.out.printf("%-10s %-20s %-10s %15s %15s%n",
                account.getAccountNumber(),
                account.getAccountHolderName(),
                account.getAccountType(),
                "$" + account.getBalance(),
                account.getCreatedDate().toLocalDate());
        }
        
        System.out.println("-".repeat(100));
        System.out.println("Total Balance Across All Accounts: $" + bankManager.getTotalBalance());
    }

    /**
     * Calculates and displays interest for an account.
     */
    private static void calculateInterestMenu() {
        System.out.println("\n=== CALCULATE INTEREST ===");
        
        try {
            System.out.print("Enter account number: ");
            String accountNumber = scanner.nextLine().trim();
            
            Account account = bankManager.findAccount(accountNumber);
            BigDecimal interest = account.calculateInterest();
            
            System.out.println("\nInterest Calculation:");
            System.out.println("Account Number: " + account.getAccountNumber());
            System.out.println("Account Type: " + account.getAccountType());
            System.out.println("Current Balance: $" + account.getBalance());
            System.out.println("Annual Interest: $" + interest);
            
            if (interest.compareTo(BigDecimal.ZERO) > 0) {
                System.out.print("\nWould you like to apply this interest to the account? (y/n): ");
                String response = scanner.nextLine().trim().toLowerCase();
                
                if ("y".equals(response) || "yes".equals(response)) {
                    account.deposit(interest);
                    System.out.println("Interest applied successfully!");
                    System.out.println("New balance: $" + account.getBalance());
                }
            }
            
        } catch (Exception e) {
            System.out.println("Error calculating interest: " + e.getMessage());
        }
    }

    /**
     * Shows system statistics.
     */
    private static void showStatistics() {
        System.out.println("\n=== ACCOUNT STATISTICS ===");
        System.out.println(bankManager.getAccountStatistics());
        System.out.println("Total System Balance: $" + bankManager.getTotalBalance());
        System.out.println("Next Account Number: " + bankManager.getNextAccountNumber());
    }
}