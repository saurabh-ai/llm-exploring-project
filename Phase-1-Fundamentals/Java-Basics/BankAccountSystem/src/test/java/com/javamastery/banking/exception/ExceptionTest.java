package com.javamastery.banking.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for custom exceptions.
 */
class ExceptionTest {
    
    @Test
    void testInsufficientFundsException() {
        String message = "Insufficient funds for withdrawal";
        InsufficientFundsException exception = new InsufficientFundsException(message);
        
        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }
    
    @Test
    void testInsufficientFundsExceptionWithCause() {
        String message = "Insufficient funds";
        Throwable cause = new RuntimeException("Original cause");
        InsufficientFundsException exception = new InsufficientFundsException(message, cause);
        
        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }
    
    @Test
    void testInvalidAccountException() {
        String message = "Account not found";
        InvalidAccountException exception = new InvalidAccountException(message);
        
        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }
    
    @Test
    void testInvalidAccountExceptionWithCause() {
        String message = "Invalid account";
        Throwable cause = new IllegalArgumentException("Bad format");
        InvalidAccountException exception = new InvalidAccountException(message, cause);
        
        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }
    
    @Test
    void testWithdrawalLimitExceededException() {
        String message = "Monthly withdrawal limit exceeded";
        WithdrawalLimitExceededException exception = new WithdrawalLimitExceededException(message);
        
        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }
    
    @Test
    void testWithdrawalLimitExceededExceptionWithCause() {
        String message = "Withdrawal limit exceeded";
        Throwable cause = new RuntimeException("Limit check failed");
        WithdrawalLimitExceededException exception = new WithdrawalLimitExceededException(message, cause);
        
        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }
}