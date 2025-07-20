package com.javamastery.banking.model;

import com.javamastery.banking.exception.InsufficientFundsException;
import com.javamastery.banking.exception.WithdrawalLimitExceededException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;

class BusinessAccountTest {
    
    private BusinessAccount account;
    
    @BeforeEach
    void setUp() {
        account = new BusinessAccount("Bob Johnson", "Johnson Corp", "12-3456789", new BigDecimal("15000.00"));
    }
    
    @Test
    void testBusinessAccountCreation() {
        assertEquals("Bob Johnson", account.getAccountHolderName());
        assertEquals("Johnson Corp", account.getBusinessName());
        assertEquals("12-3456789", account.getTaxId());
        assertEquals(new BigDecimal("15000.00"), account.getBalance());
    }
    
    @Test
    void testInvalidBusinessAccountCreation() {
        assertThrows(IllegalArgumentException.class, 
            () -> new BusinessAccount("John", null, "123", new BigDecimal("15000")));
        assertThrows(IllegalArgumentException.class, 
            () -> new BusinessAccount("John", "", "123", new BigDecimal("15000")));
        assertThrows(IllegalArgumentException.class, 
            () -> new BusinessAccount("John", "Business", null, new BigDecimal("15000")));
        assertThrows(IllegalArgumentException.class, 
            () -> new BusinessAccount("John", "Business", "", new BigDecimal("15000")));
        assertThrows(IllegalArgumentException.class, 
            () -> new BusinessAccount("John", "Business", "123", new BigDecimal("5000"))); // Below minimum
    }
    
    @Test
    void testMinimumBalanceRequirement() {
        assertThrows(IllegalArgumentException.class, 
            () -> new BusinessAccount("Test", "Test Corp", "123", new BigDecimal("5000.00")));
    }
    
    @Test
    void testWithdrawalWithinLimit() throws InsufficientFundsException, WithdrawalLimitExceededException {
        account.withdraw(new BigDecimal("2000.00"));
        assertEquals(new BigDecimal("13000.00"), account.getBalance());
    }
    
    @Test
    void testMinimumBalanceViolation() {
        assertThrows(InsufficientFundsException.class, 
            () -> account.withdraw(new BigDecimal("8000.00"))); // Would leave $7000, below $10,000 minimum
    }
    
    @Test
    void testInterestCalculation() {
        BigDecimal expectedInterest = new BigDecimal("225.00"); // $15,000 * 1.5% = $225
        assertEquals(expectedInterest, account.calculateInterest());
    }
    
    @Test
    void testApplyInterest() {
        BigDecimal originalBalance = account.getBalance();
        BigDecimal interest = account.calculateInterest();
        
        account.applyInterest();
        
        assertEquals(originalBalance.add(interest), account.getBalance());
    }
    
    @Test
    void testAccountType() {
        assertEquals("Business Account", account.getAccountType());
    }
    
    @Test
    void testAccountProperties() {
        assertEquals(new BigDecimal("10000.00"), account.getMinimumBalance());
        assertEquals(new BigDecimal("0.015"), account.getAnnualInterestRate());
    }
    
    @Test
    void testToString() {
        String toString = account.toString();
        assertTrue(toString.contains("Business Account"));
        assertTrue(toString.contains("Bob Johnson"));
        assertTrue(toString.contains("Johnson Corp"));
        assertTrue(toString.contains("12-3456789"));
        assertTrue(toString.contains("15000.00"));
        assertTrue(toString.contains(account.getAccountNumber()));
    }
}