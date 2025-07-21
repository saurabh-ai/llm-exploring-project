package com.javamastery.inventory.service;

import com.javamastery.inventory.dao.CategoryDao;
import com.javamastery.inventory.dao.InventoryDao;
import com.javamastery.inventory.dao.ProductDao;
import com.javamastery.inventory.exception.ValidationException;
import com.javamastery.inventory.model.Category;
import com.javamastery.inventory.model.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Service class for managing products with business logic and validation
 */
public class ProductService {
    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);
    
    private final ProductDao productDao;
    private final CategoryDao categoryDao;
    private final InventoryDao inventoryDao;
    
    public ProductService() {
        this.productDao = new ProductDao();
        this.categoryDao = new CategoryDao();
        this.inventoryDao = new InventoryDao();
    }
    
    public ProductService(ProductDao productDao, CategoryDao categoryDao, InventoryDao inventoryDao) {
        this.productDao = productDao;
        this.categoryDao = categoryDao;
        this.inventoryDao = inventoryDao;
    }
    
    /**
     * Create a new product
     */
    public Product createProduct(String name, String description, String sku, Long categoryId,
                               BigDecimal unitPrice, BigDecimal costPrice, Integer reorderLevel) {
        validateProductData(name, description, sku, categoryId, unitPrice, costPrice, reorderLevel);
        
        // Check for duplicate SKU
        if (productDao.existsBySku(sku)) {
            throw new ValidationException("Product with SKU '" + sku + "' already exists");
        }
        
        // Validate category exists
        if (categoryId != null && !categoryDao.existsById(categoryId)) {
            throw new ValidationException("Category with ID " + categoryId + " does not exist");
        }
        
        Product product = new Product(name, description, sku, categoryId, unitPrice, costPrice, reorderLevel);
        Product saved = productDao.save(product);
        
        // Create initial inventory record with zero stock
        inventoryDao.createOrUpdate(saved.getProductId(), "MAIN", 0, 0);
        
        logger.info("Created new product: {} ({}) with ID: {}", name, sku, saved.getProductId());
        return saved;
    }
    
    /**
     * Update an existing product
     */
    public Product updateProduct(Long productId, String name, String description, String sku,
                               Long categoryId, BigDecimal unitPrice, BigDecimal costPrice, Integer reorderLevel) {
        validateProductData(name, description, sku, categoryId, unitPrice, costPrice, reorderLevel);
        
        Optional<Product> existing = productDao.findById(productId);
        if (existing.isEmpty()) {
            throw new ValidationException("Product with ID " + productId + " not found");
        }
        
        // Check for duplicate SKU (excluding current product)
        if (productDao.existsBySkuAndNotId(sku, productId)) {
            throw new ValidationException("Product with SKU '" + sku + "' already exists");
        }
        
        // Validate category exists
        if (categoryId != null && !categoryDao.existsById(categoryId)) {
            throw new ValidationException("Category with ID " + categoryId + " does not exist");
        }
        
        Product product = existing.get();
        product.setName(name);
        product.setDescription(description);
        product.setSku(sku);
        product.setCategoryId(categoryId);
        product.setUnitPrice(unitPrice);
        product.setCostPrice(costPrice);
        product.setReorderLevel(reorderLevel);
        
        Product updated = productDao.save(product);
        
        logger.info("Updated product: {} ({}) with ID: {}", name, sku, productId);
        return updated;
    }
    
    /**
     * Delete a product
     */
    public boolean deleteProduct(Long productId) {
        Optional<Product> product = productDao.findById(productId);
        if (product.isEmpty()) {
            throw new ValidationException("Product with ID " + productId + " not found");
        }
        
        // Check if product has inventory (on hand or allocated)
        Optional<com.javamastery.inventory.model.Inventory> inventory = inventoryDao.findByProductId(productId);
        if (inventory.isPresent()) {
            com.javamastery.inventory.model.Inventory inv = inventory.get();
            if (inv.getQuantityOnHand() > 0 || inv.getQuantityAllocated() > 0) {
                throw new ValidationException("Cannot delete product '" + product.get().getName() + 
                                            "' because it has inventory on hand or allocated");
            }
        }
        
        boolean deleted = productDao.deleteById(productId);
        
        if (deleted) {
            // Clean up inventory record
            inventory.ifPresent(inv -> inventoryDao.deleteById(inv.getInventoryId()));
            logger.info("Deleted product: {} ({}) with ID: {}", product.get().getName(), 
                       product.get().getSku(), productId);
        }
        
        return deleted;
    }
    
    /**
     * Get product by ID with category information
     */
    public Optional<Product> getProductById(Long productId) {
        return productDao.findById(productId);
    }
    
    /**
     * Get product by SKU with category information
     */
    public Optional<Product> getProductBySku(String sku) {
        return productDao.findBySku(sku);
    }
    
    /**
     * Get all products with category information
     */
    public List<Product> getAllProducts() {
        return productDao.findAllWithCategory();
    }
    
    /**
     * Get products by category
     */
    public List<Product> getProductsByCategory(Long categoryId) {
        if (categoryId != null && !categoryDao.existsById(categoryId)) {
            throw new ValidationException("Category with ID " + categoryId + " does not exist");
        }
        
        return productDao.findByCategory(categoryId);
    }
    
    /**
     * Search products by multiple criteria
     */
    public List<Product> searchProducts(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return getAllProducts();
        }
        
        return productDao.searchProducts(searchTerm.trim());
    }
    
    /**
     * Get products by price range
     */
    public List<Product> getProductsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        if (minPrice == null || maxPrice == null) {
            throw new ValidationException("Price range bounds are required");
        }
        
        if (minPrice.compareTo(maxPrice) > 0) {
            throw new ValidationException("Minimum price cannot be greater than maximum price");
        }
        
        return productDao.findByPriceRange(minPrice, maxPrice);
    }
    
    /**
     * Get low stock products
     */
    public List<Product> getLowStockProducts() {
        return productDao.findLowStockProducts();
    }
    
    /**
     * Generate unique SKU
     */
    public String generateSku(String prefix) {
        if (prefix == null || prefix.trim().isEmpty()) {
            prefix = "PROD";
        }
        
        prefix = prefix.toUpperCase().replaceAll("[^A-Z0-9]", "");
        
        for (int i = 1; i <= 9999; i++) {
            String sku = String.format("%s-%04d", prefix, i);
            if (!productDao.existsBySku(sku)) {
                return sku;
            }
        }
        
        throw new ValidationException("Unable to generate unique SKU with prefix: " + prefix);
    }
    
    /**
     * Calculate product profitability
     */
    public ProductProfitability calculateProfitability(Long productId) {
        Optional<Product> product = productDao.findById(productId);
        if (product.isEmpty()) {
            throw new ValidationException("Product with ID " + productId + " not found");
        }
        
        return new ProductProfitability(product.get());
    }
    
    /**
     * Validate product data
     */
    private void validateProductData(String name, String description, String sku, Long categoryId,
                                   BigDecimal unitPrice, BigDecimal costPrice, Integer reorderLevel) {
        if (name == null || name.trim().isEmpty()) {
            throw new ValidationException("Product name is required");
        }
        
        if (name.trim().length() > 255) {
            throw new ValidationException("Product name must be 255 characters or less");
        }
        
        if (sku == null || sku.trim().isEmpty()) {
            throw new ValidationException("Product SKU is required");
        }
        
        if (sku.trim().length() > 100) {
            throw new ValidationException("Product SKU must be 100 characters or less");
        }
        
        if (description != null && description.length() > 2000) {
            throw new ValidationException("Product description must be 2000 characters or less");
        }
        
        if (unitPrice == null || unitPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new ValidationException("Unit price must be zero or positive");
        }
        
        if (costPrice == null || costPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new ValidationException("Cost price must be zero or positive");
        }
        
        if (reorderLevel == null || reorderLevel < 0) {
            throw new ValidationException("Reorder level must be zero or positive");
        }
        
        // Business rule: Unit price should generally be higher than cost price
        if (unitPrice.compareTo(costPrice) < 0) {
            logger.warn("Unit price ({}) is less than cost price ({}) for product", unitPrice, costPrice);
        }
    }
    
    /**
     * Check if product can be deleted
     */
    public boolean canDeleteProduct(Long productId) {
        try {
            Optional<Product> product = productDao.findById(productId);
            if (product.isEmpty()) {
                return false;
            }
            
            Optional<com.javamastery.inventory.model.Inventory> inventory = inventoryDao.findByProductId(productId);
            if (inventory.isPresent()) {
                com.javamastery.inventory.model.Inventory inv = inventory.get();
                return inv.getQuantityOnHand() == 0 && inv.getQuantityAllocated() == 0;
            }
            
            return true;
            
        } catch (Exception e) {
            logger.error("Error checking if product can be deleted: {}", productId, e);
            return false;
        }
    }
    
    /**
     * Inner class for product profitability analysis
     */
    public static class ProductProfitability {
        private final Product product;
        private final BigDecimal margin;
        private final BigDecimal marginPercentage;
        
        public ProductProfitability(Product product) {
            this.product = product;
            this.margin = product.getMargin();
            this.marginPercentage = product.getMarginPercentage();
        }
        
        public Product getProduct() {
            return product;
        }
        
        public BigDecimal getMargin() {
            return margin;
        }
        
        public BigDecimal getMarginPercentage() {
            return marginPercentage;
        }
        
        public boolean isProfitable() {
            return margin.compareTo(BigDecimal.ZERO) > 0;
        }
        
        public String getProfitabilityCategory() {
            if (marginPercentage.compareTo(BigDecimal.valueOf(50)) >= 0) {
                return "High Margin";
            } else if (marginPercentage.compareTo(BigDecimal.valueOf(25)) >= 0) {
                return "Medium Margin";
            } else if (marginPercentage.compareTo(BigDecimal.valueOf(10)) >= 0) {
                return "Low Margin";
            } else {
                return "Very Low Margin";
            }
        }
        
        @Override
        public String toString() {
            return "ProductProfitability{" +
                   "product=" + product.getName() +
                   ", margin=" + margin +
                   ", marginPercentage=" + marginPercentage + "%" +
                   ", category=" + getProfitabilityCategory() +
                   '}';
        }
    }
}