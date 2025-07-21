package com.javamastery.inventory;

import com.javamastery.inventory.config.DatabaseConfig;
import com.javamastery.inventory.config.DatabaseInitializer;
import com.javamastery.inventory.model.*;
import com.javamastery.inventory.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Comprehensive test application to validate service layer business logic
 */
public class ServiceTestApp {
    private static final Logger logger = LoggerFactory.getLogger(ServiceTestApp.class);
    
    public static void main(String[] args) {
        try {
            logger.info("Starting Service Layer Test Application");
            
            // Initialize database
            DatabaseConfig dbConfig = DatabaseConfig.getInstance();
            DataSource dataSource = dbConfig.getDataSource();
            
            DatabaseInitializer initializer = new DatabaseInitializer(dataSource, dbConfig.getDatabaseType());
            initializer.initializeWithSampleData();
            
            // Test services
            testCategoryService();
            testProductService();
            testInventoryService();
            
            logger.info("Service Layer Test Application completed successfully");
            
        } catch (Exception e) {
            logger.error("Service Layer Test Application failed", e);
        }
    }
    
    private static void testCategoryService() {
        logger.info("Testing CategoryService...");
        CategoryService categoryService = new CategoryService();
        
        // Test getting all categories
        List<Category> categories = categoryService.getAllCategories();
        logger.info("Found {} categories", categories.size());
        
        // Test creating a new category
        Category newCategory = categoryService.createCategory("Test Category", "Test Description");
        logger.info("Created category: {} with ID: {}", newCategory.getName(), newCategory.getCategoryId());
        
        // Test updating the category
        Category updated = categoryService.updateCategory(newCategory.getCategoryId(), 
                                                         "Updated Category", "Updated Description");
        logger.info("Updated category: {}", updated.getName());
        
        // Test category statistics
        CategoryService.CategoryStatistics stats = categoryService.getCategoryStatistics(1L);
        logger.info("Category statistics: {}", stats);
        
        // Test searching categories
        List<Category> searchResults = categoryService.searchCategories("elect");
        logger.info("Search for 'elect' found {} categories", searchResults.size());
        
        // Test deleting the test category
        boolean deleted = categoryService.deleteCategory(newCategory.getCategoryId());
        logger.info("Deleted test category: {}", deleted);
    }
    
    private static void testProductService() {
        logger.info("Testing ProductService...");
        ProductService productService = new ProductService();
        
        // Test getting all products
        List<Product> products = productService.getAllProducts();
        logger.info("Found {} products", products.size());
        
        // Test generating SKU
        String newSku = productService.generateSku("TEST");
        logger.info("Generated SKU: {}", newSku);
        
        // Test creating a new product
        Product newProduct = productService.createProduct(
            "Test Product", 
            "Test Description", 
            newSku, 
            1L, // Electronics category
            new BigDecimal("99.99"), 
            new BigDecimal("49.99"), 
            10
        );
        logger.info("Created product: {} ({}) with ID: {}", 
                   newProduct.getName(), newProduct.getSku(), newProduct.getProductId());
        
        // Test product profitability
        ProductService.ProductProfitability profitability = 
            productService.calculateProfitability(newProduct.getProductId());
        logger.info("Product profitability: {}", profitability);
        
        // Test searching products
        List<Product> searchResults = productService.searchProducts("laptop");
        logger.info("Search for 'laptop' found {} products", searchResults.size());
        
        // Test getting products by category
        List<Product> electronicsProducts = productService.getProductsByCategory(1L);
        logger.info("Electronics category has {} products", electronicsProducts.size());
        
        // Test price range search
        List<Product> midRangeProducts = productService.getProductsByPriceRange(
            new BigDecimal("50"), new BigDecimal("100"));
        logger.info("Products in $50-$100 range: {}", midRangeProducts.size());
        
        // Test updating the product
        Product updated = productService.updateProduct(
            newProduct.getProductId(),
            "Updated Test Product",
            "Updated Description",
            newProduct.getSku(),
            newProduct.getCategoryId(),
            new BigDecimal("109.99"),
            new BigDecimal("54.99"),
            15
        );
        logger.info("Updated product: {} - New price: ${}", updated.getName(), updated.getUnitPrice());
        
        // Clean up - delete the test product
        boolean deleted = productService.deleteProduct(newProduct.getProductId());
        logger.info("Deleted test product: {}", deleted);
    }
    
    private static void testInventoryService() {
        logger.info("Testing InventoryService...");
        InventoryService inventoryService = new InventoryService();
        
        // Test getting inventory summary
        InventoryService.InventorySummary summary = inventoryService.getInventorySummary();
        logger.info("Inventory summary: {}", summary);
        
        // Test getting all inventory
        List<Inventory> allInventory = inventoryService.getAllInventory();
        logger.info("Total inventory items: {}", allInventory.size());
        
        // Test stock operations on first product
        if (!allInventory.isEmpty()) {
            Inventory firstItem = allInventory.get(0);
            Long productId = firstItem.getProductId();
            String location = firstItem.getLocation();
            String user = "TestUser";
            
            logger.info("Testing stock operations on product {} at location {}", 
                       firstItem.getProductName(), location);
            
            // Get initial inventory
            Optional<Inventory> initialInventory = inventoryService.getInventoryByProduct(productId);
            if (initialInventory.isPresent()) {
                Inventory initial = initialInventory.get();
                logger.info("Initial stock: On Hand: {}, Allocated: {}, Available: {}", 
                           initial.getQuantityOnHand(), initial.getQuantityAllocated(), 
                           initial.getQuantityAvailable());
                
                // Test receiving stock
                inventoryService.receiveStock(productId, location, 50, 
                    ReferenceType.PURCHASE_ORDER, 1L, "Test receipt", user);
                logger.info("Received 50 units");
                
                // Test allocating stock
                boolean allocated = inventoryService.allocateStock(productId, location, 10, user);
                logger.info("Allocated 10 units: {}", allocated);
                
                // Test issuing stock
                inventoryService.issueStock(productId, location, 5, 
                    ReferenceType.SALES_ORDER, 1L, "Test issue", user);
                logger.info("Issued 5 units");
                
                // Test stock adjustment
                inventoryService.adjustStock(productId, location, 100, 
                    "Test adjustment to 100 units", user);
                logger.info("Adjusted stock to 100 units");
                
                // Test deallocating stock
                boolean deallocated = inventoryService.deallocateStock(productId, location, 5, user);
                logger.info("Deallocated 5 units: {}", deallocated);
                
                // Get final inventory
                Optional<Inventory> finalInventory = inventoryService.getInventoryByProduct(productId);
                if (finalInventory.isPresent()) {
                    Inventory finalInv = finalInventory.get();
                    logger.info("Final stock: On Hand: {}, Allocated: {}, Available: {}", 
                               finalInv.getQuantityOnHand(), finalInv.getQuantityAllocated(), 
                               finalInv.getQuantityAvailable());
                }
                
                // Test getting stock movement history
                List<StockMovement> movements = inventoryService.getStockMovementHistory(productId);
                logger.info("Stock movements for product {}: {}", productId, movements.size());
                
                for (StockMovement movement : movements) {
                    logger.info("Movement: {} {} units - {}", 
                               movement.getMovementType(), movement.getQuantity(), 
                               movement.getMovementDescription());
                }
            }
        }
        
        // Test low stock and out of stock
        List<Inventory> lowStock = inventoryService.getLowStockItems();
        List<Inventory> outOfStock = inventoryService.getOutOfStockItems();
        logger.info("Low stock items: {}, Out of stock items: {}", 
                   lowStock.size(), outOfStock.size());
        
        // Test locations
        List<String> locations = inventoryService.getAllLocations();
        logger.info("Available locations: {}", locations);
        
        // Test stock transfer if we have multiple locations
        if (locations.size() > 1) {
            // This would require setting up multiple locations first
            logger.info("Stock transfer testing would require multiple location setup");
        }
    }
}