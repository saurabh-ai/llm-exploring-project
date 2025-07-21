package com.javamastery.inventory.model;

/**
 * Enumeration for inventory transaction types
 */
public enum TransactionType {
    IN("Stock In"),
    OUT("Stock Out");
    
    private final String displayName;
    
    TransactionType(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    @Override
    public String toString() {
        return displayName;
    }
}