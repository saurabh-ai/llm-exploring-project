package com.javamastery.banking.exception;

/**
 * Exception thrown when an invalid account is referenced or accessed.
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
     * @param cause the cause
     */
    public InvalidAccountException(String message, Throwable cause) {
        super(message, cause);
    }
}