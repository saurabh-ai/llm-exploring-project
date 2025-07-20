package com.javamastery.banking.model;

import com.javamastery.banking.exception.InsufficientFundsException;
import com.javamastery.banking.exception.WithdrawalLimitExceededException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;

class CheckingAccountTest {
    
    private CheckingAccount account;
    
    @BeforeEach
    void setUp() {
        account = new CheckingAccount("John Doe", new BigDecimal("1000.00"));
    }
    
    @Test
    void testOverdraftWithdrawal() throws InsufficientFundsException, WithdrawalLimitExceededException {
        // Withdraw more than balance but within overdraft limit
        account.withdraw(new BigDecimal("1200.00"));
        assertEquals(new BigDecimal("-200.00"), account.getBalance());
        assertTrue(account.isOverdrawn());
    }
    
    @Test
    void testOverdraftLimit() {
        // Try to withdraw beyond overdraft limit
        assertThrows(InsufficientFundsException.class, 
            () -> account.withdraw(new BigDecimal("1600.00"))); // Balance + $500 overdraft = $1500 max
    }
    
    @Test
    void testAvailableFunds() {
        assertEquals(new BigDecimal("1500.00"), account.getAvailableFunds()); // $1000 + $500 overdraft
    }
    
    @Test
    void testMonthlyFee() throws InsufficientFundsException {
        account.applyMonthlyFee();
        assertEquals(new BigDecimal("990.00"), account.getBalance());
    }
    
    @Test
    void testMonthlyFeeWithOverdraft() throws InsufficientFundsException, WithdrawalLimitExceededException {
        // Withdraw to bring balance close to overdraft limit
        account.withdraw(new BigDecimal("1400.00")); // Balance becomes -$400
        
        account.applyMonthlyFee(); // Should work as total would be -$410, within -$500 limit
        assertEquals(new BigDecimal("-410.00"), account.getBalance());
    }
    
    @Test
    void testMonthlyFeeExceedsOverdraftLimit() throws InsufficientFundsException, WithdrawalLimitExceededException {
        // Withdraw to maximum overdraft
        account.withdraw(new BigDecimal("1495.00")); // Balance becomes -$495
        
        // Applying monthly fee would exceed overdraft limit
        assertThrows(InsufficientFundsException.class, () -> account.applyMonthlyFee());
    }
    
    @Test
    void testNoInterest() {
        assertEquals(BigDecimal.ZERO, account.calculateInterest());
    }
    
    @Test
    void testAccountType() {
        assertEquals("Checking Account", account.getAccountType());
    }
    
    @Test
    void testOverdraftProperties() {
        assertEquals(new BigDecimal("500.00"), account.getOverdraftLimit());
        assertEquals(new BigDecimal("10.00"), account.getMonthlyFee());
    }
    
    @Test
    void testIsOverdrawnInitially() {
        assertFalse(account.isOverdrawn());
    }
}