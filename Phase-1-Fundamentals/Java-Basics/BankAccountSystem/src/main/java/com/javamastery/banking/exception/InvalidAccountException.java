package com.javamastery.banking.exception;

/**
 * Exception thrown when an invalid account number is provided or account not found.
 */
public class InvalidAccountException extends Exception {
    
    /**
     * Constructs a new InvalidAccountException with the specified detail message.
     *
     * @param message the detail message
     */
    public InvalidAccountException(String message) {
        super(message);
    }

    /**
     * Constructs a new InvalidAccountException with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause the cause of the exception
     */
    public InvalidAccountException(String message, Throwable cause) {
        super(message, cause);
    }
}