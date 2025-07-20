package com.javamastery.banking.model;

import com.javamastery.banking.exception.InsufficientFundsException;
import com.javamastery.banking.exception.WithdrawalLimitExceededException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;

class AccountTest {
    
    private CheckingAccount checkingAccount;
    private SavingsAccount savingsAccount;
    private BusinessAccount businessAccount;
    
    @BeforeEach
    void setUp() {
        checkingAccount = new CheckingAccount("John Doe", new BigDecimal("1000.00"));
        savingsAccount = new SavingsAccount("Jane Smith", new BigDecimal("500.00"));
        businessAccount = new BusinessAccount("Bob Johnson", "Johnson Corp", "12-3456789", new BigDecimal("15000.00"));
    }
    
    @Test
    void testAccountCreation() {
        assertEquals("John Doe", checkingAccount.getAccountHolderName());
        assertEquals(new BigDecimal("1000.00"), checkingAccount.getBalance());
        assertNotNull(checkingAccount.getAccountNumber());
        assertNotNull(checkingAccount.getCreatedDate());
    }
    
    @Test
    void testAccountNumberGeneration() {
        Account account1 = new CheckingAccount("Test1", new BigDecimal("100"));
        Account account2 = new CheckingAccount("Test2", new BigDecimal("200"));
        
        assertNotEquals(account1.getAccountNumber(), account2.getAccountNumber());
        assertTrue(account1.getAccountNumber().matches("\\d{8}"));
        assertTrue(account2.getAccountNumber().matches("\\d{8}"));
    }
    
    @Test
    void testInvalidAccountCreation() {
        assertThrows(IllegalArgumentException.class, 
            () -> new CheckingAccount(null, new BigDecimal("100")));
        assertThrows(IllegalArgumentException.class, 
            () -> new CheckingAccount("", new BigDecimal("100")));
        assertThrows(IllegalArgumentException.class, 
            () -> new CheckingAccount("John", new BigDecimal("-100")));
        assertThrows(IllegalArgumentException.class, 
            () -> new CheckingAccount("John", null));
    }
    
    @Test
    void testDeposit() {
        checkingAccount.deposit(new BigDecimal("500.00"));
        assertEquals(new BigDecimal("1500.00"), checkingAccount.getBalance());
    }
    
    @Test
    void testInvalidDeposit() {
        assertThrows(IllegalArgumentException.class, 
            () -> checkingAccount.deposit(new BigDecimal("-100")));
        assertThrows(IllegalArgumentException.class, 
            () -> checkingAccount.deposit(BigDecimal.ZERO));
        assertThrows(IllegalArgumentException.class, 
            () -> checkingAccount.deposit(null));
    }
    
    @Test
    void testWithdraw() throws InsufficientFundsException, WithdrawalLimitExceededException {
        checkingAccount.withdraw(new BigDecimal("200.00"));
        assertEquals(new BigDecimal("800.00"), checkingAccount.getBalance());
    }
    
    @Test
    void testInvalidWithdraw() {
        assertThrows(IllegalArgumentException.class, 
            () -> checkingAccount.withdraw(new BigDecimal("-100")));
        assertThrows(IllegalArgumentException.class, 
            () -> checkingAccount.withdraw(BigDecimal.ZERO));
        assertThrows(IllegalArgumentException.class, 
            () -> checkingAccount.withdraw(null));
    }
    
    @Test
    void testAccountEquality() {
        Account account1 = new CheckingAccount("Test", new BigDecimal("100"));
        Account account2 = new CheckingAccount("Test", new BigDecimal("200"));
        
        assertEquals(account1, account1); // Same instance
        assertNotEquals(account1, account2); // Different account numbers
        assertNotEquals(account1, null);
        assertNotEquals(account1, "string");
    }
    
    @Test
    void testAccountToString() {
        String toString = checkingAccount.toString();
        assertTrue(toString.contains("Checking Account"));
        assertTrue(toString.contains("John Doe"));
        assertTrue(toString.contains("1000.00"));
        assertTrue(toString.contains(checkingAccount.getAccountNumber()));
    }
}