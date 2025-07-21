package com.javamastery.inventory.model;

/**
 * Enumeration for user roles in the inventory management system
 */
public enum UserRole {
    ADMIN("Administrator"),
    MANAGER("Manager"), 
    EMPLOYEE("Employee");
    
    private final String displayName;
    
    UserRole(String displayName) {
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