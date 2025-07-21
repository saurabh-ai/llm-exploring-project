package com.javamastery.taskmgmt.model;

/**
 * Enumeration representing the status of a task
 */
public enum TaskStatus {
    PENDING("Pending"),
    COMPLETED("Completed");
    
    private final String displayName;
    
    TaskStatus(String displayName) {
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