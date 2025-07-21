package com.javamastery.inventory.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Inventory entity representing current stock levels for products
 */
public class Inventory {
    private Long inventoryId;
    private Long productId;
    private Integer quantityOnHand;
    private Integer quantityAllocated;
    private String location;
    private LocalDateTime lastUpdated;
    
    // Optional fields for joins
    private String productName;
    private String productSku;
    private Integer reorderLevel;
    
    // Constructors
    public Inventory() {}
    
    public Inventory(Long productId, Integer quantityOnHand, Integer quantityAllocated, String location) {
        this.productId = productId;
        this.quantityOnHand = quantityOnHand;
        this.quantityAllocated = quantityAllocated;
        this.location = location;
    }
    
    public Inventory(Long inventoryId, Long productId, Integer quantityOnHand,
                    Integer quantityAllocated, String location, LocalDateTime lastUpdated) {
        this.inventoryId = inventoryId;
        this.productId = productId;
        this.quantityOnHand = quantityOnHand;
        this.quantityAllocated = quantityAllocated;
        this.location = location;
        this.lastUpdated = lastUpdated;
    }
    
    // Getters and Setters
    public Long getInventoryId() {
        return inventoryId;
    }
    
    public void setInventoryId(Long inventoryId) {
        this.inventoryId = inventoryId;
    }
    
    public Long getProductId() {
        return productId;
    }
    
    public void setProductId(Long productId) {
        this.productId = productId;
    }
    
    public Integer getQuantityOnHand() {
        return quantityOnHand;
    }
    
    public void setQuantityOnHand(Integer quantityOnHand) {
        this.quantityOnHand = quantityOnHand;
    }
    
    public Integer getQuantityAllocated() {
        return quantityAllocated;
    }
    
    public void setQuantityAllocated(Integer quantityAllocated) {
        this.quantityAllocated = quantityAllocated;
    }
    
    public String getLocation() {
        return location;
    }
    
    public void setLocation(String location) {
        this.location = location;
    }
    
    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }
    
    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
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
    
    public Integer getReorderLevel() {
        return reorderLevel;
    }
    
    public void setReorderLevel(Integer reorderLevel) {
        this.reorderLevel = reorderLevel;
    }
    
    // Business methods
    public Integer getQuantityAvailable() {
        if (quantityOnHand != null && quantityAllocated != null) {
            return quantityOnHand - quantityAllocated;
        }
        return 0;
    }
    
    public boolean isLowStock() {
        return reorderLevel != null && getQuantityAvailable() <= reorderLevel;
    }
    
    public boolean isOutOfStock() {
        return getQuantityAvailable() <= 0;
    }
    
    public String getStockStatus() {
        if (isOutOfStock()) {
            return "OUT_OF_STOCK";
        } else if (isLowStock()) {
            return "LOW_STOCK";
        } else {
            return "IN_STOCK";
        }
    }
    
    // Validation methods
    public boolean isValid() {
        return productId != null &&
               quantityOnHand != null && quantityOnHand >= 0 &&
               quantityAllocated != null && quantityAllocated >= 0 &&
               quantityAllocated <= quantityOnHand &&
               location != null && !location.trim().isEmpty();
    }
    
    public boolean canAllocate(int quantity) {
        return getQuantityAvailable() >= quantity;
    }
    
    // Object methods
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Inventory inventory = (Inventory) o;
        return Objects.equals(inventoryId, inventory.inventoryId) &&
               Objects.equals(productId, inventory.productId) &&
               Objects.equals(location, inventory.location);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(inventoryId, productId, location);
    }
    
    @Override
    public String toString() {
        return "Inventory{" +
               "inventoryId=" + inventoryId +
               ", productId=" + productId +
               ", quantityOnHand=" + quantityOnHand +
               ", quantityAllocated=" + quantityAllocated +
               ", quantityAvailable=" + getQuantityAvailable() +
               ", location='" + location + '\'' +
               ", stockStatus='" + getStockStatus() + '\'' +
               '}';
    }
}