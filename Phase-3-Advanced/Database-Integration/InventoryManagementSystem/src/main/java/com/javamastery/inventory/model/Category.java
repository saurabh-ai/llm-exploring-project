package com.javamastery.inventory.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Category entity representing product categories
 */
public class Category {
    private Long categoryId;
    private String name;
    private String description;
    private LocalDateTime createdAt;
    
    // Constructors
    public Category() {}
    
    public Category(String name, String description) {
        this.name = name;
        this.description = description;
    }
    
    public Category(Long categoryId, String name, String description, LocalDateTime createdAt) {
        this.categoryId = categoryId;
        this.name = name;
        this.description = description;
        this.createdAt = createdAt;
    }
    
    // Getters and Setters
    public Long getCategoryId() {
        return categoryId;
    }
    
    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
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
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    // Validation methods
    public boolean isValid() {
        return name != null && !name.trim().isEmpty();
    }
    
    // Object methods
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Category category = (Category) o;
        return Objects.equals(categoryId, category.categoryId) &&
               Objects.equals(name, category.name);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(categoryId, name);
    }
    
    @Override
    public String toString() {
        return "Category{" +
               "categoryId=" + categoryId +
               ", name='" + name + '\'' +
               ", description='" + description + '\'' +
               ", createdAt=" + createdAt +
               '}';
    }
}