package com.javamastery.banking.exception;

/**
 * Exception thrown when withdrawal limits are exceeded.
 */
public class WithdrawalLimitExceededException extends Exception {
    
    /**
     * Constructs a new WithdrawalLimitExceededException with the specified detail message.
     * 
     * @param message the detail message
     */
    public WithdrawalLimitExceededException(String message) {
        super(message);
    }
    
    /**
     * Constructs a new WithdrawalLimitExceededException with the specified detail message and cause.
     * 
     * @param message the detail message
     * @param cause the cause
     */
    public WithdrawalLimitExceededException(String message, Throwable cause) {
        super(message, cause);
    }
}