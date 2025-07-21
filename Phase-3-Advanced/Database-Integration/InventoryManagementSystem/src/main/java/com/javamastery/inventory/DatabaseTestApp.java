package com.javamastery.inventory;

import com.javamastery.inventory.config.DatabaseConfig;
import com.javamastery.inventory.config.DatabaseInitializer;
import com.javamastery.inventory.dao.CategoryDao;
import com.javamastery.inventory.dao.ProductDao;
import com.javamastery.inventory.dao.SupplierDao;
import com.javamastery.inventory.dao.InventoryDao;
import com.javamastery.inventory.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.util.List;

/**
 * Basic test application to validate database functionality
 */
public class DatabaseTestApp {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseTestApp.class);
    
    public static void main(String[] args) {
        try {
            logger.info("Starting Database Test Application");
            
            // Initialize database
            DatabaseConfig dbConfig = DatabaseConfig.getInstance();
            DataSource dataSource = dbConfig.getDataSource();
            
            logger.info("Database type: {}", dbConfig.getDatabaseType());
            logger.info("Connection pool stats: {}", dbConfig.getPoolStats());
            
            // Initialize schema and sample data
            DatabaseInitializer initializer = new DatabaseInitializer(dataSource, dbConfig.getDatabaseType());
            initializer.initializeWithSampleData();
            
            // Test DAOs
            testCategoryDao(dataSource);
            testSupplierDao(dataSource);
            testProductDao(dataSource);
            testInventoryDao(dataSource);
            
            logger.info("Database Test Application completed successfully");
            
        } catch (Exception e) {
            logger.error("Database Test Application failed", e);
        }
    }
    
    private static void testCategoryDao(DataSource dataSource) {
        logger.info("Testing CategoryDao...");
        CategoryDao categoryDao = new CategoryDao(dataSource);
        
        // Test finding all categories
        List<Category> categories = categoryDao.findAll();
        logger.info("Found {} categories", categories.size());
        
        // Test finding by name
        categoryDao.findByName("Electronics").ifPresent(category -> 
            logger.info("Found category: {}", category.getName()));
        
        // Test creating a new category
        Category newCategory = new Category("Test Category", "Test Description");
        Category saved = categoryDao.save(newCategory);
        logger.info("Created new category with ID: {}", saved.getCategoryId());
        
        // Test updating
        saved.setDescription("Updated description");
        categoryDao.save(saved);
        logger.info("Updated category: {}", saved.getCategoryId());
        
        // Test deleting
        categoryDao.deleteById(saved.getCategoryId());
        logger.info("Deleted category: {}", saved.getCategoryId());
    }
    
    private static void testSupplierDao(DataSource dataSource) {
        logger.info("Testing SupplierDao...");
        SupplierDao supplierDao = new SupplierDao(dataSource);
        
        // Test finding all suppliers
        List<Supplier> suppliers = supplierDao.findAll();
        logger.info("Found {} suppliers", suppliers.size());
        
        // Test search functionality
        List<Supplier> searchResults = supplierDao.searchSuppliers("tech");
        logger.info("Search for 'tech' found {} suppliers", searchResults.size());
    }
    
    private static void testProductDao(DataSource dataSource) {
        logger.info("Testing ProductDao...");
        ProductDao productDao = new ProductDao(dataSource);
        
        // Test finding all products with category
        List<Product> products = productDao.findAllWithCategory();
        logger.info("Found {} products", products.size());
        
        for (Product product : products) {
            logger.info("Product: {} ({}), Category: {}, Price: ${}", 
                       product.getName(), product.getSku(), 
                       product.getCategoryName(), product.getUnitPrice());
        }
        
        // Test finding by SKU
        productDao.findBySku("TECH-LAP-001").ifPresent(product ->
            logger.info("Found product by SKU: {} - ${}", product.getName(), product.getUnitPrice()));
    }
    
    private static void testInventoryDao(DataSource dataSource) {
        logger.info("Testing InventoryDao...");
        InventoryDao inventoryDao = new InventoryDao(dataSource);
        
        // Test finding all inventory with product info
        List<Inventory> inventoryItems = inventoryDao.findAllWithProductInfo();
        logger.info("Found {} inventory items", inventoryItems.size());
        
        for (Inventory item : inventoryItems) {
            logger.info("Inventory: {} ({}), On Hand: {}, Allocated: {}, Available: {}, Status: {}", 
                       item.getProductName(), item.getProductSku(),
                       item.getQuantityOnHand(), item.getQuantityAllocated(),
                       item.getQuantityAvailable(), item.getStockStatus());
        }
        
        // Test low stock items
        List<Inventory> lowStockItems = inventoryDao.findLowStockItems();
        logger.info("Found {} low stock items", lowStockItems.size());
    }
}