package com.javamastery.inventory.model;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Purchase Order Item entity representing individual items within a purchase order
 */
public class PurchaseOrderItem {
    private Long poiId;
    private Long poId;
    private Long productId;
    private Integer quantityOrdered;
    private Integer quantityReceived;
    private BigDecimal unitCost;
    
    // Optional fields for joins
    private String productName;
    private String productSku;
    
    // Constructors
    public PurchaseOrderItem() {}
    
    public PurchaseOrderItem(Long poId, Long productId, Integer quantityOrdered, BigDecimal unitCost) {
        this.poId = poId;
        this.productId = productId;
        this.quantityOrdered = quantityOrdered;
        this.quantityReceived = 0;
        this.unitCost = unitCost;
    }
    
    public PurchaseOrderItem(Long poiId, Long poId, Long productId, Integer quantityOrdered,
                           Integer quantityReceived, BigDecimal unitCost) {
        this.poiId = poiId;
        this.poId = poId;
        this.productId = productId;
        this.quantityOrdered = quantityOrdered;
        this.quantityReceived = quantityReceived;
        this.unitCost = unitCost;
    }
    
    // Getters and Setters
    public Long getPoiId() {
        return poiId;
    }
    
    public void setPoiId(Long poiId) {
        this.poiId = poiId;
    }
    
    public Long getPoId() {
        return poId;
    }
    
    public void setPoId(Long poId) {
        this.poId = poId;
    }
    
    public Long getProductId() {
        return productId;
    }
    
    public void setProductId(Long productId) {
        this.productId = productId;
    }
    
    public Integer getQuantityOrdered() {
        return quantityOrdered;
    }
    
    public void setQuantityOrdered(Integer quantityOrdered) {
        this.quantityOrdered = quantityOrdered;
    }
    
    public Integer getQuantityReceived() {
        return quantityReceived;
    }
    
    public void setQuantityReceived(Integer quantityReceived) {
        this.quantityReceived = quantityReceived;
    }
    
    public BigDecimal getUnitCost() {
        return unitCost;
    }
    
    public void setUnitCost(BigDecimal unitCost) {
        this.unitCost = unitCost;
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
    public BigDecimal getLineTotal() {
        if (quantityOrdered != null && unitCost != null) {
            return unitCost.multiply(BigDecimal.valueOf(quantityOrdered));
        }
        return BigDecimal.ZERO;
    }
    
    public Integer getQuantityPending() {
        if (quantityOrdered != null && quantityReceived != null) {
            return quantityOrdered - quantityReceived;
        }
        return quantityOrdered != null ? quantityOrdered : 0;
    }
    
    public boolean isFullyReceived() {
        return quantityReceived != null && quantityOrdered != null &&
               quantityReceived.equals(quantityOrdered);
    }
    
    public boolean isPartiallyReceived() {
        return quantityReceived != null && quantityReceived > 0 && !isFullyReceived();
    }
    
    public boolean canReceive(int quantity) {
        return quantity > 0 && quantity <= getQuantityPending();
    }
    
    public void receiveQuantity(int quantity) {
        if (canReceive(quantity)) {
            this.quantityReceived = (this.quantityReceived != null ? this.quantityReceived : 0) + quantity;
        } else {
            throw new IllegalArgumentException("Cannot receive " + quantity + " items. Only " + 
                                             getQuantityPending() + " items are pending.");
        }
    }
    
    public double getReceivePercentage() {
        if (quantityOrdered != null && quantityOrdered > 0) {
            int received = quantityReceived != null ? quantityReceived : 0;
            return (double) received / quantityOrdered * 100.0;
        }
        return 0.0;
    }
    
    // Validation methods
    public boolean isValid() {
        return poId != null &&
               productId != null &&
               quantityOrdered != null && quantityOrdered > 0 &&
               quantityReceived != null && quantityReceived >= 0 &&
               quantityReceived <= quantityOrdered &&
               unitCost != null && unitCost.compareTo(BigDecimal.ZERO) >= 0;
    }
    
    // Object methods
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PurchaseOrderItem that = (PurchaseOrderItem) o;
        return Objects.equals(poiId, that.poiId) &&
               Objects.equals(poId, that.poId) &&
               Objects.equals(productId, that.productId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(poiId, poId, productId);
    }
    
    @Override
    public String toString() {
        return "PurchaseOrderItem{" +
               "poiId=" + poiId +
               ", poId=" + poId +
               ", productId=" + productId +
               ", quantityOrdered=" + quantityOrdered +
               ", quantityReceived=" + quantityReceived +
               ", unitCost=" + unitCost +
               ", lineTotal=" + getLineTotal() +
               '}';
    }
}