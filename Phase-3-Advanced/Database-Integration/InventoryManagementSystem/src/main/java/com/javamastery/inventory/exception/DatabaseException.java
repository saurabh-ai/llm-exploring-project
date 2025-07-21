package com.javamastery.inventory.exception;

/**
 * Exception thrown for database-related errors
 */
public class DatabaseException extends InventoryException {
    
    public DatabaseException(String message) {
        super(message);
    }
    
    public DatabaseException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public DatabaseException(Throwable cause) {
        super(cause);
    }
}