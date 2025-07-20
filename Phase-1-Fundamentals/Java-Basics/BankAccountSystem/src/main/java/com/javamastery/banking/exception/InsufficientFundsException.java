package com.javamastery.banking.exception;

/**
 * Exception thrown when an account has insufficient funds for a transaction.
 */
public class InsufficientFundsException extends Exception {
    
    /**
     * Constructs a new InsufficientFundsException with the specified detail message.
     *
     * @param message the detail message
     */
    public InsufficientFundsException(String message) {
        super(message);
    }

    /**
     * Constructs a new InsufficientFundsException with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause the cause of the exception
     */
    public InsufficientFundsException(String message, Throwable cause) {
        super(message, cause);
    }
}