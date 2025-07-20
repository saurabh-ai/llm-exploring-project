package com.javamastery.banking.model;

import com.javamastery.banking.exception.InsufficientFundsException;
import com.javamastery.banking.exception.WithdrawalLimitExceededException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for all account types.
 */
class AccountTest {
    
    private CheckingAccount checkingAccount;
    private SavingsAccount savingsAccount;
    private BusinessAccount businessAccount;

    @BeforeEach
    void setUp() {
        checkingAccount = new CheckingAccount("10000001", "John Doe", new BigDecimal("1000.00"));
        savingsAccount = new SavingsAccount("10000002", "Jane Smith", new BigDecimal("5000.00"));
        businessAccount = new BusinessAccount("10000003", "Bob Wilson", "Wilson LLC", "12-3456789", new BigDecimal("15000.00"));
    }

    @Test
    void testCheckingAccountCreation() {
        assertEquals("10000001", checkingAccount.getAccountNumber());
        assertEquals("John Doe", checkingAccount.getAccountHolderName());
        assertEquals(new BigDecimal("1000.00"), checkingAccount.getBalance());
        assertEquals("Checking", checkingAccount.getAccountType());
        assertEquals(BigDecimal.ZERO, checkingAccount.calculateInterest());
    }

    @Test
    void testSavingsAccountCreation() {
        assertEquals("10000002", savingsAccount.getAccountNumber());
        assertEquals("Jane Smith", savingsAccount.getAccountHolderName());
        assertEquals(new BigDecimal("5000.00"), savingsAccount.getBalance());
        assertEquals("Savings", savingsAccount.getAccountType());
        assertTrue(savingsAccount.calculateInterest().compareTo(BigDecimal.ZERO) > 0);
    }

    @Test
    void testBusinessAccountCreation() {
        assertEquals("10000003", businessAccount.getAccountNumber());
        assertEquals("Bob Wilson", businessAccount.getAccountHolderName());
        assertEquals("Wilson LLC", businessAccount.getBusinessName());
        assertEquals("12-3456789", businessAccount.getTaxId());
        assertEquals(new BigDecimal("15000.00"), businessAccount.getBalance());
        assertEquals("Business", businessAccount.getAccountType());
        assertTrue(businessAccount.calculateInterest().compareTo(BigDecimal.ZERO) > 0);
    }

    @Test
    void testDeposit() {
        BigDecimal initialBalance = checkingAccount.getBalance();
        BigDecimal depositAmount = new BigDecimal("500.00");
        
        checkingAccount.deposit(depositAmount);
        
        assertEquals(initialBalance.add(depositAmount), checkingAccount.getBalance());
    }

    @Test
    void testInvalidDeposit() {
        assertThrows(IllegalArgumentException.class, () -> {
            checkingAccount.deposit(new BigDecimal("-100.00"));
        });
        
        assertThrows(IllegalArgumentException.class, () -> {
            checkingAccount.deposit(null);
        });
    }

    @Test
    void testCheckingAccountWithdraw() throws InsufficientFundsException, WithdrawalLimitExceededException {
        BigDecimal initialBalance = checkingAccount.getBalance();
        BigDecimal withdrawAmount = new BigDecimal("200.00");
        
        checkingAccount.withdraw(withdrawAmount);
        
        assertEquals(initialBalance.subtract(withdrawAmount), checkingAccount.getBalance());
    }

    @Test
    void testCheckingAccountOverdraft() throws InsufficientFundsException, WithdrawalLimitExceededException {
        // Test withdrawal that would use overdraft
        BigDecimal withdrawAmount = new BigDecimal("1200.00"); // More than balance but within overdraft
        
        checkingAccount.withdraw(withdrawAmount);
        
        assertTrue(checkingAccount.isOverdrawn());
        assertEquals(new BigDecimal("200.00"), checkingAccount.getOverdrawnAmount());
    }

    @Test
    void testCheckingAccountOverdraftLimit() {
        // Test withdrawal exceeding overdraft limit
        BigDecimal withdrawAmount = new BigDecimal("1600.00"); // Balance + overdraft = 1500
        
        assertThrows(InsufficientFundsException.class, () -> {
            checkingAccount.withdraw(withdrawAmount);
        });
    }

    @Test
    void testSavingsAccountWithdraw() throws InsufficientFundsException, WithdrawalLimitExceededException {
        BigDecimal initialBalance = savingsAccount.getBalance();
        BigDecimal withdrawAmount = new BigDecimal("1000.00");
        
        savingsAccount.withdraw(withdrawAmount);
        
        assertEquals(initialBalance.subtract(withdrawAmount), savingsAccount.getBalance());
        assertEquals(1, savingsAccount.getWithdrawalCount());
    }

    @Test
    void testSavingsAccountMinimumBalance() {
        // Test withdrawal that would violate minimum balance
        BigDecimal withdrawAmount = new BigDecimal("5000.00"); // Would leave less than $100
        
        assertThrows(InsufficientFundsException.class, () -> {
            savingsAccount.withdraw(withdrawAmount);
        });
    }

    @Test
    void testSavingsAccountWithdrawalLimit() throws InsufficientFundsException, WithdrawalLimitExceededException {
        // Make 6 withdrawals (the limit)
        for (int i = 0; i < 6; i++) {
            savingsAccount.withdraw(new BigDecimal("100.00"));
        }
        
        assertEquals(6, savingsAccount.getWithdrawalCount());
        
        // 7th withdrawal should fail
        assertThrows(WithdrawalLimitExceededException.class, () -> {
            savingsAccount.withdraw(new BigDecimal("100.00"));
        });
    }

    @Test
    void testSavingsAccountInterestCalculation() {
        BigDecimal expectedInterest = savingsAccount.getBalance().multiply(new BigDecimal("0.025"));
        assertEquals(expectedInterest.setScale(2, java.math.RoundingMode.HALF_UP), 
                    savingsAccount.calculateInterest());
    }

    @Test
    void testBusinessAccountWithdraw() throws InsufficientFundsException, WithdrawalLimitExceededException {
        BigDecimal initialBalance = businessAccount.getBalance();
        BigDecimal withdrawAmount = new BigDecimal("2000.00");
        
        businessAccount.withdraw(withdrawAmount);
        
        assertEquals(initialBalance.subtract(withdrawAmount), businessAccount.getBalance());
    }

    @Test
    void testBusinessAccountMinimumBalance() {
        // Test withdrawal that would violate minimum balance
        BigDecimal withdrawAmount = new BigDecimal("10000.00"); // Would leave less than $10,000
        
        assertThrows(InsufficientFundsException.class, () -> {
            businessAccount.withdraw(withdrawAmount);
        });
    }

    @Test
    void testBusinessAccountInterestCalculation() {
        BigDecimal expectedInterest = businessAccount.getBalance().multiply(new BigDecimal("0.015"));
        assertEquals(expectedInterest.setScale(2, java.math.RoundingMode.HALF_UP), 
                    businessAccount.calculateInterest());
    }

    @Test
    void testAccountEquality() {
        CheckingAccount account1 = new CheckingAccount("12345678", "Test User", new BigDecimal("100"));
        CheckingAccount account2 = new CheckingAccount("12345678", "Different Name", new BigDecimal("200"));
        CheckingAccount account3 = new CheckingAccount("87654321", "Test User", new BigDecimal("100"));
        
        assertEquals(account1, account2); // Same account number
        assertNotEquals(account1, account3); // Different account number
    }

    @Test
    void testAccountToString() {
        String checkingString = checkingAccount.toString();
        assertTrue(checkingString.contains("10000001"));
        assertTrue(checkingString.contains("John Doe"));
        assertTrue(checkingString.contains("1000.00"));
        
        String businessString = businessAccount.toString();
        assertTrue(businessString.contains("Wilson LLC"));
        assertTrue(businessString.contains("12-3456789"));
    }
}