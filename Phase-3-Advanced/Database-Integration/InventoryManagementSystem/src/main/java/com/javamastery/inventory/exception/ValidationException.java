package com.javamastery.inventory.exception;

/**
 * Exception thrown for data validation errors
 */
public class ValidationException extends InventoryException {
    
    public ValidationException(String message) {
        super(message);
    }
    
    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public ValidationException(Throwable cause) {
        super(cause);
    }
}