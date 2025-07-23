package com.javamastery.ecommerce.notification.entity;

public enum NotificationType {
    ORDER_CONFIRMATION("Order Confirmation"),
    ORDER_SHIPPED("Order Shipped"),
    ORDER_DELIVERED("Order Delivered"),
    ORDER_CANCELLED("Order Cancelled"),
    PAYMENT_CONFIRMATION("Payment Confirmation"),
    PAYMENT_FAILED("Payment Failed"),
    PAYMENT_REFUNDED("Payment Refunded"),
    ACCOUNT_CREATED("Account Created"),
    PASSWORD_RESET("Password Reset"),
    PROMOTIONAL("Promotional"),
    SYSTEM_ALERT("System Alert");
    
    private final String displayName;
    
    NotificationType(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}