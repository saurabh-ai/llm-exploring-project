package com.javamastery.contacts.exception;

/**
 * Exception thrown when contact validation fails.
 * 
 * Learning Objectives:
 * - Custom unchecked exceptions
 * - Input validation error handling
 * - Field-specific error reporting
 */
public class ContactValidationException extends RuntimeException {
    
    private final String fieldName;
    private final String fieldValue;
    
    public ContactValidationException(String message) {
        super(message);
        this.fieldName = "unknown";
        this.fieldValue = "unknown";
    }
    
    public ContactValidationException(String message, String fieldName, String fieldValue) {
        super(message);
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }
    
    public ContactValidationException(String message, Throwable cause) {
        super(message, cause);
        this.fieldName = "unknown";
        this.fieldValue = "unknown";
    }
    
    public String getFieldName() {
        return fieldName;
    }
    
    public String getFieldValue() {
        return fieldValue;
    }
    
    @Override
    public String toString() {
        return String.format("%s: Field '%s' with value '%s' - %s", 
                getClass().getSimpleName(), fieldName, fieldValue, getMessage());
    }
}