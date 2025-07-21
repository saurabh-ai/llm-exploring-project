package com.javamastery.inventory.model;

/**
 * Enumeration of purchase order statuses
 */
public enum PurchaseOrderStatus {
    DRAFT("Draft"),
    SENT("Sent"),
    CONFIRMED("Confirmed"),
    RECEIVED("Received"),
    CANCELLED("Cancelled");
    
    private final String displayName;
    
    PurchaseOrderStatus(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public boolean canTransitionTo(PurchaseOrderStatus newStatus) {
        return switch (this) {
            case DRAFT -> newStatus == SENT || newStatus == CANCELLED;
            case SENT -> newStatus == CONFIRMED || newStatus == CANCELLED;
            case CONFIRMED -> newStatus == RECEIVED || newStatus == CANCELLED;
            case RECEIVED, CANCELLED -> false;
        };
    }
    
    public boolean isEditable() {
        return this == DRAFT;
    }
    
    public boolean isActive() {
        return this != CANCELLED && this != RECEIVED;
    }
    
    @Override
    public String toString() {
        return displayName;
    }
}