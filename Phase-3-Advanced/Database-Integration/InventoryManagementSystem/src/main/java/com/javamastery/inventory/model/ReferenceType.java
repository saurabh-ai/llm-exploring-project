package com.javamastery.inventory.model;

/**
 * Enumeration of reference types for stock movements
 */
public enum ReferenceType {
    PURCHASE_ORDER("Purchase Order"),
    SALES_ORDER("Sales Order"),
    ADJUSTMENT("Manual Adjustment"),
    TRANSFER("Stock Transfer");
    
    private final String displayName;
    
    ReferenceType(String displayName) {
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