package com.javamastery.inventory.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Stock Movement entity representing audit trail of all inventory movements
 */
public class StockMovement {
    private Long movementId;
    private Long productId;
    private MovementType movementType;
    private Integer quantity;
    private ReferenceType referenceType;
    private Long referenceId;
    private String notes;
    private String createdBy;
    private LocalDateTime createdAt;
    
    // Optional fields for joins
    private String productName;
    private String productSku;
    
    // Constructors
    public StockMovement() {}
    
    public StockMovement(Long productId, MovementType movementType, Integer quantity,
                        ReferenceType referenceType, Long referenceId, String notes, String createdBy) {
        this.productId = productId;
        this.movementType = movementType;
        this.quantity = quantity;
        this.referenceType = referenceType;
        this.referenceId = referenceId;
        this.notes = notes;
        this.createdBy = createdBy;
    }
    
    public StockMovement(Long movementId, Long productId, MovementType movementType,
                        Integer quantity, ReferenceType referenceType, Long referenceId,
                        String notes, String createdBy, LocalDateTime createdAt) {
        this.movementId = movementId;
        this.productId = productId;
        this.movementType = movementType;
        this.quantity = quantity;
        this.referenceType = referenceType;
        this.referenceId = referenceId;
        this.notes = notes;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
    }
    
    // Getters and Setters
    public Long getMovementId() {
        return movementId;
    }
    
    public void setMovementId(Long movementId) {
        this.movementId = movementId;
    }
    
    public Long getProductId() {
        return productId;
    }
    
    public void setProductId(Long productId) {
        this.productId = productId;
    }
    
    public MovementType getMovementType() {
        return movementType;
    }
    
    public void setMovementType(MovementType movementType) {
        this.movementType = movementType;
    }
    
    public Integer getQuantity() {
        return quantity;
    }
    
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
    
    public ReferenceType getReferenceType() {
        return referenceType;
    }
    
    public void setReferenceType(ReferenceType referenceType) {
        this.referenceType = referenceType;
    }
    
    public Long getReferenceId() {
        return referenceId;
    }
    
    public void setReferenceId(Long referenceId) {
        this.referenceId = referenceId;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    public String getCreatedBy() {
        return createdBy;
    }
    
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public String getProductName() {
        return productName;
    }
    
    public void setProductName(String productName) {
        this.productName = productName;
    }
    
    public String getProductSku() {
        return productSku;
    }
    
    public void setProductSku(String productSku) {
        this.productSku = productSku;
    }
    
    // Business methods
    public boolean isInbound() {
        return movementType == MovementType.IN;
    }
    
    public boolean isOutbound() {
        return movementType == MovementType.OUT;
    }
    
    public boolean isAdjustment() {
        return movementType == MovementType.ADJUSTMENT;
    }
    
    public String getMovementDescription() {
        StringBuilder description = new StringBuilder();
        description.append(movementType.getDisplayName())
                  .append(" - ")
                  .append(Math.abs(quantity))
                  .append(" units");
        
        if (referenceType != null) {
            description.append(" (").append(referenceType.getDisplayName());
            if (referenceId != null) {
                description.append(" #").append(referenceId);
            }
            description.append(")");
        }
        
        return description.toString();
    }
    
    // Validation methods
    public boolean isValid() {
        return productId != null &&
               movementType != null &&
               quantity != null && quantity != 0 &&
               referenceType != null &&
               createdBy != null && !createdBy.trim().isEmpty();
    }
    
    // Object methods
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StockMovement that = (StockMovement) o;
        return Objects.equals(movementId, that.movementId) &&
               Objects.equals(productId, that.productId) &&
               Objects.equals(createdAt, that.createdAt);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(movementId, productId, createdAt);
    }
    
    @Override
    public String toString() {
        return "StockMovement{" +
               "movementId=" + movementId +
               ", productId=" + productId +
               ", movementType=" + movementType +
               ", quantity=" + quantity +
               ", referenceType=" + referenceType +
               ", referenceId=" + referenceId +
               ", createdBy='" + createdBy + '\'' +
               ", createdAt=" + createdAt +
               '}';
    }
}