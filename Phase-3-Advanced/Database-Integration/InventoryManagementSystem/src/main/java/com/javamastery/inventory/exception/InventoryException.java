package com.javamastery.inventory.exception;

/**
 * Base exception for all inventory management system exceptions
 */
public class InventoryException extends RuntimeException {
    
    public InventoryException(String message) {
        super(message);
    }
    
    public InventoryException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public InventoryException(Throwable cause) {
        super(cause);
    }
}