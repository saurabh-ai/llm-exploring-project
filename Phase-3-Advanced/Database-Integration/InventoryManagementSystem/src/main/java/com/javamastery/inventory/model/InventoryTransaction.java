package com.javamastery.inventory.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * InventoryTransaction entity representing inventory transactions
 */
public class InventoryTransaction {
    private Long transactionId;
    private Long productId;
    private TransactionType transactionType;
    private Integer quantity;
    private String reason;
    private LocalDateTime transactionDate;
    private Long userId;
    
    // Optional fields for joins
    private String productName;
    private String username;
    
    // Constructors
    public InventoryTransaction() {}
    
    public InventoryTransaction(Long productId, TransactionType transactionType, 
                               Integer quantity, String reason, Long userId) {
        this.productId = productId;
        this.transactionType = transactionType;
        this.quantity = quantity;
        this.reason = reason;
        this.userId = userId;
    }
    
    public InventoryTransaction(Long transactionId, Long productId, TransactionType transactionType,
                               Integer quantity, String reason, LocalDateTime transactionDate, Long userId) {
        this.transactionId = transactionId;
        this.productId = productId;
        this.transactionType = transactionType;
        this.quantity = quantity;
        this.reason = reason;
        this.transactionDate = transactionDate;
        this.userId = userId;
    }
    
    // Getters and Setters
    public Long getTransactionId() {
        return transactionId;
    }
    
    public void setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
    }
    
    public Long getProductId() {
        return productId;
    }
    
    public void setProductId(Long productId) {
        this.productId = productId;
    }
    
    public TransactionType getTransactionType() {
        return transactionType;
    }
    
    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }
    
    public Integer getQuantity() {
        return quantity;
    }
    
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
    
    public String getReason() {
        return reason;
    }
    
    public void setReason(String reason) {
        this.reason = reason;
    }
    
    public LocalDateTime getTransactionDate() {
        return transactionDate;
    }
    
    public void setTransactionDate(LocalDateTime transactionDate) {
        this.transactionDate = transactionDate;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public String getProductName() {
        return productName;
    }
    
    public void setProductName(String productName) {
        this.productName = productName;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    // equals and hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InventoryTransaction that = (InventoryTransaction) o;
        return Objects.equals(transactionId, that.transactionId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(transactionId);
    }
    
    @Override
    public String toString() {
        return "InventoryTransaction{" +
               "transactionId=" + transactionId +
               ", productId=" + productId +
               ", transactionType=" + transactionType +
               ", quantity=" + quantity +
               ", reason='" + reason + '\'' +
               ", transactionDate=" + transactionDate +
               ", userId=" + userId +
               ", productName='" + productName + '\'' +
               ", username='" + username + '\'' +
               '}';
    }
}