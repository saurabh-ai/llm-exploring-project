package com.javamastery.banking.service;

import com.javamastery.banking.exception.InsufficientFundsException;
import com.javamastery.banking.exception.InvalidAccountException;
import com.javamastery.banking.exception.WithdrawalLimitExceededException;
import com.javamastery.banking.model.Account;
import com.javamastery.banking.model.BusinessAccount;
import com.javamastery.banking.model.CheckingAccount;
import com.javamastery.banking.model.SavingsAccount;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for BankAccountManager service.
 */
class BankAccountManagerTest {
    
    private BankAccountManager bankManager;

    @BeforeEach
    void setUp() {
        bankManager = new BankAccountManager();
    }

    @Test
    void testCreateCheckingAccount() {
        String accountNumber = bankManager.createAccount("John Doe", "checking", new BigDecimal("500.00"));
        
        assertNotNull(accountNumber);
        assertEquals(8, accountNumber.length());
        assertTrue(accountNumber.matches("\\d{8}")); // Just check it's 8 digits
        
        try {
            Account account = bankManager.findAccount(accountNumber);
            assertEquals("John Doe", account.getAccountHolderName());
            assertEquals("Checking", account.getAccountType());
            assertEquals(new BigDecimal("500.00"), account.getBalance());
            assertTrue(account instanceof CheckingAccount);
        } catch (InvalidAccountException e) {
            fail("Account should exist");
        }
    }

    @Test
    void testCreateSavingsAccount() {
        String accountNumber = bankManager.createAccount("Jane Smith", "savings", new BigDecimal("1000.00"));
        
        assertNotNull(accountNumber);
        try {
            Account account = bankManager.findAccount(accountNumber);
            assertEquals("Jane Smith", account.getAccountHolderName());
            assertEquals("Savings", account.getAccountType());
            assertTrue(account instanceof SavingsAccount);
        } catch (InvalidAccountException e) {
            fail("Account should exist");
        }
    }

    @Test
    void testCreateBusinessAccount() {
        String accountNumber = bankManager.createAccount("Bob Wilson", "business", new BigDecimal("15000.00"), 
                                                        "Wilson LLC", "12-3456789");
        
        assertNotNull(accountNumber);
        try {
            Account account = bankManager.findAccount(accountNumber);
            assertEquals("Bob Wilson", account.getAccountHolderName());
            assertEquals("Business", account.getAccountType());
            assertTrue(account instanceof BusinessAccount);
            
            BusinessAccount businessAccount = (BusinessAccount) account;
            assertEquals("Wilson LLC", businessAccount.getBusinessName());
            assertEquals("12-3456789", businessAccount.getTaxId());
        } catch (InvalidAccountException e) {
            fail("Account should exist");
        }
    }

    @Test
    void testCreateAccountInvalidName() {
        assertThrows(IllegalArgumentException.class, () -> {
            bankManager.createAccount("", "checking", new BigDecimal("100.00"));
        });
        
        assertThrows(IllegalArgumentException.class, () -> {
            bankManager.createAccount(null, "checking", new BigDecimal("100.00"));
        });
    }

    @Test
    void testCreateAccountInvalidType() {
        assertThrows(IllegalArgumentException.class, () -> {
            bankManager.createAccount("John Doe", "invalid", new BigDecimal("100.00"));
        });
    }

    @Test
    void testCreateAccountInsufficientDeposit() {
        // Savings account with less than minimum
        assertThrows(IllegalArgumentException.class, () -> {
            bankManager.createAccount("Jane Smith", "savings", new BigDecimal("50.00"));
        });
        
        // Business account with less than minimum
        assertThrows(IllegalArgumentException.class, () -> {
            bankManager.createAccount("Bob Wilson", "business", new BigDecimal("5000.00"), 
                                    "Wilson LLC", "12-3456789");
        });
    }

    @Test
    void testCreateBusinessAccountInvalidData() {
        // Invalid business name
        assertThrows(IllegalArgumentException.class, () -> {
            bankManager.createAccount("Bob Wilson", "business", new BigDecimal("15000.00"), 
                                    "", "12-3456789");
        });
        
        // Invalid tax ID
        assertThrows(IllegalArgumentException.class, () -> {
            bankManager.createAccount("Bob Wilson", "business", new BigDecimal("15000.00"), 
                                    "Wilson LLC", "invalid-tax-id");
        });
    }

    @Test
    void testDeposit() throws InvalidAccountException {
        String accountNumber = bankManager.createAccount("John Doe", "checking", new BigDecimal("500.00"));
        
        bankManager.deposit(accountNumber, new BigDecimal("200.00"));
        
        Account account = bankManager.findAccount(accountNumber);
        assertEquals(new BigDecimal("700.00"), account.getBalance());
    }

    @Test
    void testDepositInvalidAccount() {
        assertThrows(InvalidAccountException.class, () -> {
            bankManager.deposit("99999999", new BigDecimal("100.00"));
        });
    }

    @Test
    void testDepositInvalidAmount() throws InvalidAccountException {
        String accountNumber = bankManager.createAccount("John Doe", "checking", new BigDecimal("500.00"));
        
        assertThrows(IllegalArgumentException.class, () -> {
            bankManager.deposit(accountNumber, new BigDecimal("-100.00"));
        });
        
        assertThrows(IllegalArgumentException.class, () -> {
            bankManager.deposit(accountNumber, null);
        });
    }

    @Test
    void testWithdraw() throws InvalidAccountException, InsufficientFundsException, WithdrawalLimitExceededException {
        String accountNumber = bankManager.createAccount("John Doe", "checking", new BigDecimal("500.00"));
        
        bankManager.withdraw(accountNumber, new BigDecimal("200.00"));
        
        Account account = bankManager.findAccount(accountNumber);
        assertEquals(new BigDecimal("300.00"), account.getBalance());
    }

    @Test
    void testWithdrawInsufficientFunds() {
        String accountNumber = bankManager.createAccount("John Doe", "savings", new BigDecimal("200.00"));
        
        assertThrows(InsufficientFundsException.class, () -> {
            bankManager.withdraw(accountNumber, new BigDecimal("150.00")); // Would violate minimum balance
        });
    }

    @Test
    void testTransfer() throws InvalidAccountException, InsufficientFundsException, WithdrawalLimitExceededException {
        String fromAccount = bankManager.createAccount("John Doe", "checking", new BigDecimal("1000.00"));
        String toAccount = bankManager.createAccount("Jane Smith", "savings", new BigDecimal("500.00"));
        
        bankManager.transfer(fromAccount, toAccount, new BigDecimal("300.00"));
        
        Account fromAcc = bankManager.findAccount(fromAccount);
        Account toAcc = bankManager.findAccount(toAccount);
        
        assertEquals(new BigDecimal("700.00"), fromAcc.getBalance());
        assertEquals(new BigDecimal("800.00"), toAcc.getBalance());
    }

    @Test
    void testTransferSameAccount() {
        String accountNumber = bankManager.createAccount("John Doe", "checking", new BigDecimal("1000.00"));
        
        assertThrows(IllegalArgumentException.class, () -> {
            bankManager.transfer(accountNumber, accountNumber, new BigDecimal("100.00"));
        });
    }

    @Test
    void testTransferInvalidAccount() {
        String validAccount = bankManager.createAccount("John Doe", "checking", new BigDecimal("1000.00"));
        
        assertThrows(InvalidAccountException.class, () -> {
            bankManager.transfer("99999999", validAccount, new BigDecimal("100.00"));
        });
        
        assertThrows(InvalidAccountException.class, () -> {
            bankManager.transfer(validAccount, "99999999", new BigDecimal("100.00"));
        });
    }

    @Test
    void testFindAccount() throws InvalidAccountException {
        String accountNumber = bankManager.createAccount("John Doe", "checking", new BigDecimal("500.00"));
        
        Account account = bankManager.findAccount(accountNumber);
        
        assertNotNull(account);
        assertEquals(accountNumber, account.getAccountNumber());
        assertEquals("John Doe", account.getAccountHolderName());
    }

    @Test
    void testFindAccountNotFound() {
        assertThrows(InvalidAccountException.class, () -> {
            bankManager.findAccount("99999999");
        });
    }

    @Test
    void testFindAccountInvalidFormat() {
        assertThrows(InvalidAccountException.class, () -> {
            bankManager.findAccount("invalid");
        });
    }

    @Test
    void testGetAllAccounts() {
        assertTrue(bankManager.getAllAccounts().isEmpty());
        
        bankManager.createAccount("John Doe", "checking", new BigDecimal("500.00"));
        bankManager.createAccount("Jane Smith", "savings", new BigDecimal("1000.00"));
        
        List<Account> accounts = bankManager.getAllAccounts();
        assertEquals(2, accounts.size());
    }

    @Test
    void testGetAccountsByType() {
        bankManager.createAccount("John Doe", "checking", new BigDecimal("500.00"));
        bankManager.createAccount("Jane Smith", "savings", new BigDecimal("1000.00"));
        bankManager.createAccount("Bob Wilson", "checking", new BigDecimal("300.00"));
        
        List<Account> checkingAccounts = bankManager.getAccountsByType("checking");
        List<Account> savingsAccounts = bankManager.getAccountsByType("savings");
        List<Account> businessAccounts = bankManager.getAccountsByType("business");
        
        assertEquals(2, checkingAccounts.size());
        assertEquals(1, savingsAccounts.size());
        assertEquals(0, businessAccounts.size());
    }

    @Test
    void testGetAccountsByHolder() {
        bankManager.createAccount("John Doe", "checking", new BigDecimal("500.00"));
        bankManager.createAccount("john doe", "savings", new BigDecimal("1000.00")); // Different case
        bankManager.createAccount("Jane Smith", "checking", new BigDecimal("300.00"));
        
        List<Account> johnAccounts = bankManager.getAccountsByHolder("John Doe");
        List<Account> janeAccounts = bankManager.getAccountsByHolder("Jane Smith");
        
        assertEquals(2, johnAccounts.size()); // Should find both despite case difference
        assertEquals(1, janeAccounts.size());
    }

    @Test
    void testGetTotalBalance() {
        assertEquals(BigDecimal.ZERO, bankManager.getTotalBalance());
        
        bankManager.createAccount("John Doe", "checking", new BigDecimal("500.00"));
        bankManager.createAccount("Jane Smith", "savings", new BigDecimal("1000.00"));
        
        assertEquals(new BigDecimal("1500.00"), bankManager.getTotalBalance());
    }

    @Test
    void testGetAccountStatistics() {
        String stats = bankManager.getAccountStatistics();
        assertTrue(stats.contains("Total: 0"));
        
        bankManager.createAccount("John Doe", "checking", new BigDecimal("500.00"));
        bankManager.createAccount("Jane Smith", "savings", new BigDecimal("1000.00"));
        bankManager.createAccount("Bob Wilson", "business", new BigDecimal("15000.00"), 
                                "Wilson LLC", "12-3456789");
        
        stats = bankManager.getAccountStatistics();
        assertTrue(stats.contains("Checking: 1"));
        assertTrue(stats.contains("Savings: 1"));
        assertTrue(stats.contains("Business: 1"));
        assertTrue(stats.contains("Total: 3"));
    }

    @Test
    void testAccountNumberGeneration() {
        String account1 = bankManager.createAccount("Alice Johnson", "checking", new BigDecimal("100.00"));
        String account2 = bankManager.createAccount("Bob Smith", "checking", new BigDecimal("100.00"));
        
        assertNotEquals(account1, account2);
        
        int num1 = Integer.parseInt(account1);
        int num2 = Integer.parseInt(account2);
        assertEquals(1, num2 - num1);
    }
}