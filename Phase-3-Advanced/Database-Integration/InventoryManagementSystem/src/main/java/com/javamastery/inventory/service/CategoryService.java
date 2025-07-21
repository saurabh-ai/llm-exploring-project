package com.javamastery.inventory.service;

import com.javamastery.inventory.dao.CategoryDao;
import com.javamastery.inventory.dao.ProductDao;
import com.javamastery.inventory.exception.ValidationException;
import com.javamastery.inventory.model.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

/**
 * Service class for managing categories with business logic and validation
 */
public class CategoryService {
    private static final Logger logger = LoggerFactory.getLogger(CategoryService.class);
    
    private final CategoryDao categoryDao;
    private final ProductDao productDao;
    
    public CategoryService() {
        this.categoryDao = new CategoryDao();
        this.productDao = new ProductDao();
    }
    
    public CategoryService(CategoryDao categoryDao, ProductDao productDao) {
        this.categoryDao = categoryDao;
        this.productDao = productDao;
    }
    
    /**
     * Create a new category
     */
    public Category createCategory(String name, String description) {
        validateCategoryData(name, description);
        
        // Check for duplicate name
        if (categoryDao.existsByName(name)) {
            throw new ValidationException("Category with name '" + name + "' already exists");
        }
        
        Category category = new Category(name, description);
        Category saved = categoryDao.save(category);
        
        logger.info("Created new category: {} with ID: {}", name, saved.getCategoryId());
        return saved;
    }
    
    /**
     * Update an existing category
     */
    public Category updateCategory(Long categoryId, String name, String description) {
        validateCategoryData(name, description);
        
        Optional<Category> existing = categoryDao.findById(categoryId);
        if (existing.isEmpty()) {
            throw new ValidationException("Category with ID " + categoryId + " not found");
        }
        
        // Check for duplicate name (excluding current category)
        if (categoryDao.existsByNameAndNotId(name, categoryId)) {
            throw new ValidationException("Category with name '" + name + "' already exists");
        }
        
        Category category = existing.get();
        category.setName(name);
        category.setDescription(description);
        
        Category updated = categoryDao.save(category);
        
        logger.info("Updated category: {} with ID: {}", name, categoryId);
        return updated;
    }
    
    /**
     * Delete a category
     */
    public boolean deleteCategory(Long categoryId) {
        Optional<Category> category = categoryDao.findById(categoryId);
        if (category.isEmpty()) {
            throw new ValidationException("Category with ID " + categoryId + " not found");
        }
        
        // Check if category has products
        long productCount = productDao.countByCategory(categoryId);
        if (productCount > 0) {
            throw new ValidationException("Cannot delete category '" + category.get().getName() + 
                                        "' because it has " + productCount + " products assigned");
        }
        
        boolean deleted = categoryDao.deleteById(categoryId);
        
        if (deleted) {
            logger.info("Deleted category: {} with ID: {}", category.get().getName(), categoryId);
        }
        
        return deleted;
    }
    
    /**
     * Get category by ID
     */
    public Optional<Category> getCategoryById(Long categoryId) {
        return categoryDao.findById(categoryId);
    }
    
    /**
     * Get category by name
     */
    public Optional<Category> getCategoryByName(String name) {
        return categoryDao.findByName(name);
    }
    
    /**
     * Get all categories
     */
    public List<Category> getAllCategories() {
        return categoryDao.findAll();
    }
    
    /**
     * Search categories by name pattern
     */
    public List<Category> searchCategories(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return getAllCategories();
        }
        
        return categoryDao.findByNameContaining(searchTerm.trim());
    }
    
    /**
     * Validate category data
     */
    private void validateCategoryData(String name, String description) {
        if (name == null || name.trim().isEmpty()) {
            throw new ValidationException("Category name is required");
        }
        
        if (name.trim().length() > 255) {
            throw new ValidationException("Category name must be 255 characters or less");
        }
        
        if (description != null && description.length() > 1000) {
            throw new ValidationException("Category description must be 1000 characters or less");
        }
    }
    
    /**
     * Check if category can be deleted
     */
    public boolean canDeleteCategory(Long categoryId) {
        try {
            Optional<Category> category = categoryDao.findById(categoryId);
            if (category.isEmpty()) {
                return false;
            }
            
            long productCount = productDao.countByCategory(categoryId);
            return productCount == 0;
            
        } catch (Exception e) {
            logger.error("Error checking if category can be deleted: {}", categoryId, e);
            return false;
        }
    }
    
    /**
     * Get category statistics
     */
    public CategoryStatistics getCategoryStatistics(Long categoryId) {
        Optional<Category> category = categoryDao.findById(categoryId);
        if (category.isEmpty()) {
            throw new ValidationException("Category with ID " + categoryId + " not found");
        }
        
        long productCount = productDao.countByCategory(categoryId);
        
        return new CategoryStatistics(category.get(), productCount);
    }
    
    /**
     * Inner class for category statistics
     */
    public static class CategoryStatistics {
        private final Category category;
        private final long productCount;
        
        public CategoryStatistics(Category category, long productCount) {
            this.category = category;
            this.productCount = productCount;
        }
        
        public Category getCategory() {
            return category;
        }
        
        public long getProductCount() {
            return productCount;
        }
        
        @Override
        public String toString() {
            return "CategoryStatistics{" +
                   "category=" + category.getName() +
                   ", productCount=" + productCount +
                   '}';
        }
    }
}