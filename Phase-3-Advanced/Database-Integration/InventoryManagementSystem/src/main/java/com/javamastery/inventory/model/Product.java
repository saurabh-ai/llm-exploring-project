package com.javamastery.inventory.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Product entity representing inventory products
 */
public class Product {
    private Long productId;
    private String name;
    private String description;
    private String sku;
    private Long categoryId;
    private BigDecimal unitPrice;
    private BigDecimal costPrice;
    private Integer reorderLevel;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Optional fields for joins
    private String categoryName;
    
    // Constructors
    public Product() {}
    
    public Product(String name, String description, String sku, Long categoryId,
                  BigDecimal unitPrice, BigDecimal costPrice, Integer reorderLevel) {
        this.name = name;
        this.description = description;
        this.sku = sku;
        this.categoryId = categoryId;
        this.unitPrice = unitPrice;
        this.costPrice = costPrice;
        this.reorderLevel = reorderLevel;
    }
    
    public Product(Long productId, String name, String description, String sku,
                  Long categoryId, BigDecimal unitPrice, BigDecimal costPrice,
                  Integer reorderLevel, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.productId = productId;
        this.name = name;
        this.description = description;
        this.sku = sku;
        this.categoryId = categoryId;
        this.unitPrice = unitPrice;
        this.costPrice = costPrice;
        this.reorderLevel = reorderLevel;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    
    // Getters and Setters
    public Long getProductId() {
        return productId;
    }
    
    public void setProductId(Long productId) {
        this.productId = productId;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getSku() {
        return sku;
    }
    
    public void setSku(String sku) {
        this.sku = sku;
    }
    
    public Long getCategoryId() {
        return categoryId;
    }
    
    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }
    
    public BigDecimal getUnitPrice() {
        return unitPrice;
    }
    
    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }
    
    public BigDecimal getCostPrice() {
        return costPrice;
    }
    
    public void setCostPrice(BigDecimal costPrice) {
        this.costPrice = costPrice;
    }
    
    public Integer getReorderLevel() {
        return reorderLevel;
    }
    
    public void setReorderLevel(Integer reorderLevel) {
        this.reorderLevel = reorderLevel;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public String getCategoryName() {
        return categoryName;
    }
    
    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
    
    // Business methods
    public BigDecimal getMargin() {
        if (unitPrice != null && costPrice != null) {
            return unitPrice.subtract(costPrice);
        }
        return BigDecimal.ZERO;
    }
    
    public BigDecimal getMarginPercentage() {
        if (costPrice != null && costPrice.compareTo(BigDecimal.ZERO) > 0) {
            return getMargin().divide(costPrice, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
        }
        return BigDecimal.ZERO;
    }
    
    // Validation methods
    public boolean isValid() {
        return name != null && !name.trim().isEmpty() &&
               sku != null && !sku.trim().isEmpty() &&
               unitPrice != null && unitPrice.compareTo(BigDecimal.ZERO) >= 0 &&
               costPrice != null && costPrice.compareTo(BigDecimal.ZERO) >= 0 &&
               reorderLevel != null && reorderLevel >= 0;
    }
    
    public boolean isPriceValid() {
        return unitPrice != null && costPrice != null &&
               unitPrice.compareTo(BigDecimal.ZERO) >= 0 &&
               costPrice.compareTo(BigDecimal.ZERO) >= 0;
    }
    
    // Object methods
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return Objects.equals(productId, product.productId) &&
               Objects.equals(sku, product.sku);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(productId, sku);
    }
    
    @Override
    public String toString() {
        return "Product{" +
               "productId=" + productId +
               ", name='" + name + '\'' +
               ", sku='" + sku + '\'' +
               ", unitPrice=" + unitPrice +
               ", costPrice=" + costPrice +
               ", reorderLevel=" + reorderLevel +
               '}';
    }
}