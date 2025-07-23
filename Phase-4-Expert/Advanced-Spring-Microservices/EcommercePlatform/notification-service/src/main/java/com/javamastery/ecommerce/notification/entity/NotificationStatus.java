package com.javamastery.ecommerce.notification.entity;

public enum NotificationStatus {
    PENDING("Pending"),
    SENDING("Sending"),
    SENT("Sent"),
    FAILED("Failed"),
    CANCELLED("Cancelled"),
    SCHEDULED("Scheduled");
    
    private final String description;
    
    NotificationStatus(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
}