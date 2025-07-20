package com.javamastery.contacts.exception;

/**
 * Exception thrown when attempting to add a duplicate contact.
 * 
 * Learning Objectives:
 * - Specific business rule exceptions  
 * - Exception context information
 * - Conflict resolution in data operations
 */
public class DuplicateContactException extends RuntimeException {
    
    private final String conflictingField;
    private final String conflictingValue;
    
    public DuplicateContactException(String message) {
        super(message);
        this.conflictingField = "unknown";
        this.conflictingValue = "unknown";
    }
    
    public DuplicateContactException(String message, String conflictingField, String conflictingValue) {
        super(message);
        this.conflictingField = conflictingField;
        this.conflictingValue = conflictingValue;
    }
    
    public String getConflictingField() {
        return conflictingField;
    }
    
    public String getConflictingValue() {
        return conflictingValue;
    }
    
    @Override
    public String toString() {
        return String.format("%s: Duplicate %s '%s' - %s", 
                getClass().getSimpleName(), conflictingField, conflictingValue, getMessage());
    }
}