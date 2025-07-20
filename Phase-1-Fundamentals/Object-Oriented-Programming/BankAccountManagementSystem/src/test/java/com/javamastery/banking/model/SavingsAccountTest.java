package com.javamastery.banking.model;

import com.javamastery.banking.exception.InsufficientFundsException;
import com.javamastery.banking.exception.WithdrawalLimitExceededException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;

class SavingsAccountTest {
    
    private SavingsAccount account;
    
    @BeforeEach
    void setUp() {
        account = new SavingsAccount("Jane Smith", new BigDecimal("1000.00"));
    }
    
    @Test
    void testMinimumBalanceRequirement() {
        assertThrows(IllegalArgumentException.class, 
            () -> new SavingsAccount("Test", new BigDecimal("50.00")));
    }
    
    @Test
    void testWithdrawalWithinLimit() throws InsufficientFundsException, WithdrawalLimitExceededException {
        account.withdraw(new BigDecimal("100.00"));
        assertEquals(new BigDecimal("900.00"), account.getBalance());
        assertEquals(1, account.getCurrentMonthlyWithdrawals());
        assertEquals(5, account.getRemainingWithdrawals());
    }
    
    @Test
    void testMinimumBalanceViolation() {
        assertThrows(InsufficientFundsException.class, 
            () -> account.withdraw(new BigDecimal("950.00"))); // Would leave $50, below $100 minimum
    }
    
    @Test
    void testMonthlyWithdrawalLimit() throws InsufficientFundsException, WithdrawalLimitExceededException {
        // Make 6 withdrawals (maximum allowed)
        for (int i = 0; i < 6; i++) {
            account.withdraw(new BigDecimal("50.00"));
        }
        
        assertEquals(6, account.getCurrentMonthlyWithdrawals());
        assertEquals(0, account.getRemainingWithdrawals());
        
        // 7th withdrawal should fail
        assertThrows(WithdrawalLimitExceededException.class, 
            () -> account.withdraw(new BigDecimal("50.00")));
    }
    
    @Test
    void testResetMonthlyWithdrawals() throws InsufficientFundsException, WithdrawalLimitExceededException {
        // Make maximum withdrawals
        for (int i = 0; i < 6; i++) {
            account.withdraw(new BigDecimal("50.00"));
        }
        
        // Reset and try again
        account.resetMonthlyWithdrawals();
        assertEquals(0, account.getCurrentMonthlyWithdrawals());
        assertEquals(6, account.getRemainingWithdrawals());
        
        // Should be able to withdraw again
        account.withdraw(new BigDecimal("50.00"));
        assertEquals(1, account.getCurrentMonthlyWithdrawals());
    }
    
    @Test
    void testInterestCalculation() {
        BigDecimal expectedInterest = new BigDecimal("25.00"); // $1000 * 2.5% = $25
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
        assertEquals("Savings Account", account.getAccountType());
    }
    
    @Test
    void testAccountProperties() {
        assertEquals(new BigDecimal("100.00"), account.getMinimumBalance());
        assertEquals(new BigDecimal("0.025"), account.getAnnualInterestRate());
        assertEquals(6, account.getMaxMonthlyWithdrawals());
        assertEquals(0, account.getCurrentMonthlyWithdrawals());
        assertEquals(6, account.getRemainingWithdrawals());
    }
}