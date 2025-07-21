package com.javamastery.inventory.model;

/**
 * Enumeration of stock movement types
 */
public enum MovementType {
    IN("Stock In"),
    OUT("Stock Out"),
    ADJUSTMENT("Stock Adjustment"),
    TRANSFER("Stock Transfer");
    
    private final String displayName;
    
    MovementType(String displayName) {
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