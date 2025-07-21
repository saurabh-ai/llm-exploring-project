package com.javamastery.contacts.exception;

/**
 * Exception thrown when data persistence operations fail.
 * 
 * Learning Objectives:
 * - Custom exception creation and inheritance
 * - Exception chaining and cause tracking
 * - Proper exception documentation
 */
public class DataPersistenceException extends Exception {
    
    private final String operation;
    private final String filename;
    
    public DataPersistenceException(String message) {
        super(message);
        this.operation = "unknown";
        this.filename = "unknown";
    }
    
    public DataPersistenceException(String message, String operation, String filename) {
        super(message);
        this.operation = operation;
        this.filename = filename;
    }
    
    public DataPersistenceException(String message, Throwable cause) {
        super(message, cause);
        this.operation = "unknown";
        this.filename = "unknown";
    }
    
    public DataPersistenceException(String message, String operation, String filename, Throwable cause) {
        super(message, cause);
        this.operation = operation;
        this.filename = filename;
    }
    
    public String getOperation() {
        return operation;
    }
    
    public String getFilename() {
        return filename;
    }
    
    @Override
    public String toString() {
        return String.format("%s: Operation '%s' failed on file '%s' - %s", 
                getClass().getSimpleName(), operation, filename, getMessage());
    }
}