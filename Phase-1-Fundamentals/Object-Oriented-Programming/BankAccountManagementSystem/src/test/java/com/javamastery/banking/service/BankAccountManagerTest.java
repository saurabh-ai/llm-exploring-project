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
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.List;

class BankAccountManagerTest {
    
    private BankAccountManager manager;
    private CheckingAccount checkingAccount;
    private SavingsAccount savingsAccount;
    private BusinessAccount businessAccount;
    
    @BeforeEach
    void setUp() {
        manager = new BankAccountManager();
        checkingAccount = manager.createCheckingAccount("John Doe", new BigDecimal("1000.00"));
        savingsAccount = manager.createSavingsAccount("Jane Smith", new BigDecimal("2000.00"));
        businessAccount = manager.createBusinessAccount("Bob Johnson", "Johnson Corp", "12-3456789", new BigDecimal("15000.00"));
    }
    
    @Test
    void testAccountCreation() {
        assertEquals(3, manager.getAccountCount());
        
        List<Account> accounts = manager.getAllAccounts();
        assertEquals(3, accounts.size());
        
        assertTrue(accounts.stream().anyMatch(a -> a instanceof CheckingAccount));
        assertTrue(accounts.stream().anyMatch(a -> a instanceof SavingsAccount));
        assertTrue(accounts.stream().anyMatch(a -> a instanceof BusinessAccount));
    }
    
    @Test
    void testFindAccount() {
        var foundAccount = manager.findAccount(checkingAccount.getAccountNumber());
        assertTrue(foundAccount.isPresent());
        assertEquals(checkingAccount, foundAccount.get());
        
        var notFound = manager.findAccount("99999999");
        assertTrue(notFound.isEmpty());
    }
    
    @Test
    void testFindAccountsByHolder() {
        // Create another account for John Doe
        manager.createSavingsAccount("John Doe", new BigDecimal("5000.00"));
        
        List<Account> johnAccounts = manager.findAccountsByHolder("John Doe");
        assertEquals(2, johnAccounts.size());
        
        List<Account> janeAccounts = manager.findAccountsByHolder("Jane Smith");
        assertEquals(1, janeAccounts.size());
        
        List<Account> nonExistent = manager.findAccountsByHolder("Non Existent");
        assertEquals(0, nonExistent.size());
    }
    
    @Test
    void testDeposit() throws InvalidAccountException {
        BigDecimal originalBalance = checkingAccount.getBalance();
        manager.deposit(checkingAccount.getAccountNumber(), new BigDecimal("500.00"));
        
        assertEquals(originalBalance.add(new BigDecimal("500.00")), checkingAccount.getBalance());
    }
    
    @Test
    void testDepositInvalidAccount() {
        assertThrows(InvalidAccountException.class, 
            () -> manager.deposit("99999999", new BigDecimal("100.00")));
    }
    
    @Test
    void testWithdraw() throws InvalidAccountException, InsufficientFundsException, WithdrawalLimitExceededException {
        BigDecimal originalBalance = checkingAccount.getBalance();
        manager.withdraw(checkingAccount.getAccountNumber(), new BigDecimal("200.00"));
        
        assertEquals(originalBalance.subtract(new BigDecimal("200.00")), checkingAccount.getBalance());
    }
    
    @Test
    void testWithdrawInvalidAccount() {
        assertThrows(InvalidAccountException.class, 
            () -> manager.withdraw("99999999", new BigDecimal("100.00")));
    }
    
    @Test
    void testWithdrawInsufficientFunds() {
        assertThrows(InsufficientFundsException.class, 
            () -> manager.withdraw(savingsAccount.getAccountNumber(), new BigDecimal("3000.00")));
    }
    
    @Test
    void testTransfer() throws InvalidAccountException, InsufficientFundsException, WithdrawalLimitExceededException {
        // Create two accounts for the same holder
        CheckingAccount account1 = manager.createCheckingAccount("Transfer Test", new BigDecimal("1000.00"));
        SavingsAccount account2 = manager.createSavingsAccount("Transfer Test", new BigDecimal("500.00"));
        
        BigDecimal transferAmount = new BigDecimal("300.00");
        manager.transfer(account1.getAccountNumber(), account2.getAccountNumber(), transferAmount);
        
        assertEquals(new BigDecimal("700.00"), account1.getBalance());
        assertEquals(new BigDecimal("800.00"), account2.getBalance());
    }
    
    @Test
    void testTransferBetweenDifferentHolders() {
        assertThrows(InvalidAccountException.class, 
            () -> manager.transfer(checkingAccount.getAccountNumber(), savingsAccount.getAccountNumber(), new BigDecimal("100.00")));
    }
    
    @Test
    void testTransferInvalidAccounts() {
        assertThrows(InvalidAccountException.class, 
            () -> manager.transfer("99999999", checkingAccount.getAccountNumber(), new BigDecimal("100.00")));
        
        assertThrows(InvalidAccountException.class, 
            () -> manager.transfer(checkingAccount.getAccountNumber(), "99999999", new BigDecimal("100.00")));
    }
    
    @Test
    void testGetBalance() throws InvalidAccountException {
        BigDecimal balance = manager.getBalance(checkingAccount.getAccountNumber());
        assertEquals(checkingAccount.getBalance(), balance);
    }
    
    @Test
    void testGetBalanceInvalidAccount() {
        assertThrows(InvalidAccountException.class, 
            () -> manager.getBalance("99999999"));
    }
    
    @Test
    void testTotalBalance() {
        BigDecimal expectedTotal = checkingAccount.getBalance()
                .add(savingsAccount.getBalance())
                .add(businessAccount.getBalance());
        
        assertEquals(expectedTotal, manager.getTotalBalance());
    }
    
    @Test
    void testCalculateInterestForAllAccounts() {
        // This method should not throw exceptions and should calculate interest for eligible accounts
        assertDoesNotThrow(() -> manager.calculateInterestForAllAccounts());
        
        // Verify that interest was applied to savings and business accounts
        // The balance should have increased for these accounts
        BigDecimal savingsBalance = savingsAccount.getBalance();
        BigDecimal businessBalance = businessAccount.getBalance();
        
        // For checking account, balance should remain the same (no interest)
        assertEquals(new BigDecimal("1000.00"), checkingAccount.getBalance());
        
        // For savings and business accounts, balance should have increased
        assertTrue(savingsBalance.compareTo(new BigDecimal("2000.00")) > 0);
        assertTrue(businessBalance.compareTo(new BigDecimal("15000.00")) > 0);
    }
    
    @Test
    void testApplyMonthlyFees() {
        BigDecimal originalCheckingBalance = checkingAccount.getBalance();
        BigDecimal originalSavingsBalance = savingsAccount.getBalance();
        BigDecimal originalBusinessBalance = businessAccount.getBalance();
        
        assertDoesNotThrow(() -> manager.applyMonthlyFees());
        
        // Checking account should have fee applied
        assertEquals(originalCheckingBalance.subtract(new BigDecimal("10.00")), checkingAccount.getBalance());
        
        // Savings and business accounts should remain unchanged
        assertEquals(originalSavingsBalance, savingsAccount.getBalance());
        assertEquals(originalBusinessBalance, businessAccount.getBalance());
    }
    
    @Test
    void testResetMonthlyWithdrawals() {
        assertDoesNotThrow(() -> manager.resetMonthlyWithdrawals());
        
        // Verify that savings account withdrawal count is reset
        assertEquals(0, savingsAccount.getCurrentMonthlyWithdrawals());
        assertEquals(6, savingsAccount.getRemainingWithdrawals());
    }
}