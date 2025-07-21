package com.javamastery.inventory.cli;

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
import java.util.Scanner;

/**
 * Command Line Interface for the Inventory Management System
 * Provides text-based interaction for all system features
 */
public class InventoryCLI {
    private static final Logger logger = LoggerFactory.getLogger(InventoryCLI.class);
    
    private final Scanner scanner;
    private final CategoryService categoryService;
    private final ProductService productService;
    private final InventoryService inventoryService;
    private boolean running = true;
    
    public InventoryCLI() {
        this.scanner = new Scanner(System.in);
        this.categoryService = new CategoryService();
        this.productService = new ProductService();
        this.inventoryService = new InventoryService();
    }
    
    public static void main(String[] args) {
        try {
            System.out.println("=== Inventory Management System - CLI Interface ===");
            System.out.println();
            
            // Initialize database
            DatabaseConfig dbConfig = DatabaseConfig.getInstance();
            DataSource dataSource = dbConfig.getDataSource();
            
            DatabaseInitializer initializer = new DatabaseInitializer(dataSource, dbConfig.getDatabaseType());
            if (!initializer.schemaExists()) {
                System.out.println("Initializing database with sample data...");
                initializer.initializeWithSampleData();
                System.out.println("Database initialized successfully!");
            }
            
            // Start CLI
            InventoryCLI cli = new InventoryCLI();
            cli.run();
            
        } catch (Exception e) {
            logger.error("CLI Application failed", e);
            System.err.println("Error: " + e.getMessage());
        }
    }
    
    public void run() {
        while (running) {
            showMainMenu();
            int choice = getIntInput("Select an option: ");
            handleMainMenuChoice(choice);
        }
        
        scanner.close();
        System.out.println("Thank you for using Inventory Management System!");
    }
    
    private void showMainMenu() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("MAIN MENU");
        System.out.println("=".repeat(50));
        System.out.println("1. Product Management");
        System.out.println("2. Category Management");
        System.out.println("3. Inventory Management");
        System.out.println("4. Reports");
        System.out.println("5. System Information");
        System.out.println("0. Exit");
        System.out.println("=".repeat(50));
    }
    
    private void handleMainMenuChoice(int choice) {
        switch (choice) {
            case 1 -> productManagementMenu();
            case 2 -> categoryManagementMenu();
            case 3 -> inventoryManagementMenu();
            case 4 -> reportsMenu();
            case 5 -> showSystemInfo();
            case 0 -> {
                running = false;
                System.out.println("Exiting...");
            }
            default -> System.out.println("Invalid option. Please try again.");
        }
    }
    
    private void productManagementMenu() {
        while (true) {
            System.out.println("\n--- Product Management ---");
            System.out.println("1. List all products");
            System.out.println("2. Search products");
            System.out.println("3. Add new product");
            System.out.println("4. Update product");
            System.out.println("5. View product details");
            System.out.println("0. Back to main menu");
            
            int choice = getIntInput("Select option: ");
            switch (choice) {
                case 1 -> listAllProducts();
                case 2 -> searchProducts();
                case 3 -> addNewProduct();
                case 4 -> updateProduct();
                case 5 -> viewProductDetails();
                case 0 -> { return; }
                default -> System.out.println("Invalid option.");
            }
        }
    }
    
    private void categoryManagementMenu() {
        while (true) {
            System.out.println("\n--- Category Management ---");
            System.out.println("1. List all categories");
            System.out.println("2. Add new category");
            System.out.println("3. Update category");
            System.out.println("4. View category statistics");
            System.out.println("0. Back to main menu");
            
            int choice = getIntInput("Select option: ");
            switch (choice) {
                case 1 -> listAllCategories();
                case 2 -> addNewCategory();
                case 3 -> updateCategory();
                case 4 -> viewCategoryStatistics();
                case 0 -> { return; }
                default -> System.out.println("Invalid option.");
            }
        }
    }
    
    private void inventoryManagementMenu() {
        while (true) {
            System.out.println("\n--- Inventory Management ---");
            System.out.println("1. View inventory summary");
            System.out.println("2. Check stock levels");
            System.out.println("3. Receive stock");
            System.out.println("4. Issue stock");
            System.out.println("5. Adjust stock");
            System.out.println("6. Allocate stock");
            System.out.println("7. View stock movements");
            System.out.println("8. Low stock report");
            System.out.println("0. Back to main menu");
            
            int choice = getIntInput("Select option: ");
            switch (choice) {
                case 1 -> showInventorySummary();
                case 2 -> checkStockLevels();
                case 3 -> receiveStock();
                case 4 -> issueStock();
                case 5 -> adjustStock();
                case 6 -> allocateStock();
                case 7 -> viewStockMovements();
                case 8 -> showLowStockReport();
                case 0 -> { return; }
                default -> System.out.println("Invalid option.");
            }
        }
    }
    
    private void reportsMenu() {
        while (true) {
            System.out.println("\n--- Reports ---");
            System.out.println("1. Inventory valuation");
            System.out.println("2. Low stock items");
            System.out.println("3. Out of stock items");
            System.out.println("4. Category summary");
            System.out.println("5. Stock movement history");
            System.out.println("0. Back to main menu");
            
            int choice = getIntInput("Select option: ");
            switch (choice) {
                case 1 -> showInventoryValuation();
                case 2 -> showLowStockItems();
                case 3 -> showOutOfStockItems();
                case 4 -> showCategorySummary();
                case 5 -> showStockMovementHistory();
                case 0 -> { return; }
                default -> System.out.println("Invalid option.");
            }
        }
    }
    
    // Product Management Methods
    private void listAllProducts() {
        List<Product> products = productService.getAllProducts();
        System.out.println("\n--- All Products ---");
        System.out.printf("%-5s %-15s %-30s %-15s %-10s%n", "ID", "SKU", "Name", "Category", "Price");
        System.out.println("-".repeat(75));
        
        for (Product product : products) {
            System.out.printf("%-5d %-15s %-30s %-15s $%-9.2f%n",
                product.getProductId(),
                product.getSku(),
                truncate(product.getName(), 29),
                truncate(product.getCategoryName(), 14),
                product.getUnitPrice()
            );
        }
        System.out.println("Total products: " + products.size());
    }
    
    private void searchProducts() {
        String searchTerm = getStringInput("Enter search term: ");
        List<Product> products = productService.searchProducts(searchTerm);
        
        if (products.isEmpty()) {
            System.out.println("No products found matching: " + searchTerm);
        } else {
            System.out.println("\n--- Search Results ---");
            System.out.printf("%-5s %-15s %-30s %-10s%n", "ID", "SKU", "Name", "Price");
            System.out.println("-".repeat(60));
            
            for (Product product : products) {
                System.out.printf("%-5d %-15s %-30s $%-9.2f%n",
                    product.getProductId(),
                    product.getSku(),
                    truncate(product.getName(), 29),
                    product.getUnitPrice()
                );
            }
        }
    }
    
    private void addNewProduct() {
        System.out.println("\n--- Add New Product ---");
        
        // Show available categories
        List<Category> categories = categoryService.getAllCategories();
        System.out.println("Available categories:");
        for (Category category : categories) {
            System.out.printf("%d. %s%n", category.getCategoryId(), category.getName());
        }
        
        try {
            String name = getStringInput("Product name: ");
            String description = getStringInput("Description: ");
            String skuPrefix = getStringInput("SKU prefix (3-4 chars): ").toUpperCase();
            Long categoryId = getLongInput("Category ID: ");
            BigDecimal unitPrice = getBigDecimalInput("Unit price: ");
            BigDecimal costPrice = getBigDecimalInput("Cost price: ");
            int reorderLevel = getIntInput("Reorder level: ");
            
            Product product = productService.createProduct(name, description, skuPrefix, 
                categoryId, unitPrice, costPrice, reorderLevel);
                
            System.out.println("Product created successfully!");
            System.out.printf("ID: %d, SKU: %s, Name: %s%n", 
                product.getProductId(), product.getSku(), product.getName());
                
        } catch (Exception e) {
            System.out.println("Error creating product: " + e.getMessage());
        }
    }
    
    private void updateProduct() {
        Long productId = getLongInput("Enter product ID to update: ");
        
        Optional<Product> productOpt = productService.getProductById(productId);
        if (productOpt.isEmpty()) {
            System.out.println("Product not found.");
            return;
        }
        
        Product product = productOpt.get();
        System.out.println("Current product: " + product.getName());
        System.out.println("Enter new values (press Enter to keep current value):");
        
        try {
            String name = getStringInputOptional("Name [" + product.getName() + "]: ");
            if (name.isEmpty()) name = product.getName();
            
            String description = getStringInputOptional("Description [" + product.getDescription() + "]: ");
            if (description.isEmpty()) description = product.getDescription();
            
            String unitPriceStr = getStringInputOptional("Unit price [" + product.getUnitPrice() + "]: ");
            BigDecimal unitPrice = unitPriceStr.isEmpty() ? product.getUnitPrice() : new BigDecimal(unitPriceStr);
            
            String costPriceStr = getStringInputOptional("Cost price [" + product.getCostPrice() + "]: ");
            BigDecimal costPrice = costPriceStr.isEmpty() ? product.getCostPrice() : new BigDecimal(costPriceStr);
            
            String reorderStr = getStringInputOptional("Reorder level [" + product.getReorderLevel() + "]: ");
            int reorderLevel = reorderStr.isEmpty() ? product.getReorderLevel() : Integer.parseInt(reorderStr);
            
            Product updated = productService.updateProduct(productId, name, description, 
                product.getSku(), product.getCategoryId(), unitPrice, costPrice, reorderLevel);
                
            System.out.println("Product updated successfully: " + updated.getName());
            
        } catch (Exception e) {
            System.out.println("Error updating product: " + e.getMessage());
        }
    }
    
    private void viewProductDetails() {
        Long productId = getLongInput("Enter product ID: ");
        
        Optional<Product> productOpt = productService.getProductById(productId);
        if (productOpt.isEmpty()) {
            System.out.println("Product not found.");
            return;
        }
        
        Product product = productOpt.get();
        System.out.println("\n--- Product Details ---");
        System.out.println("ID: " + product.getProductId());
        System.out.println("SKU: " + product.getSku());
        System.out.println("Name: " + product.getName());
        System.out.println("Description: " + product.getDescription());
        System.out.println("Category: " + product.getCategoryName());
        System.out.println("Unit Price: $" + product.getUnitPrice());
        System.out.println("Cost Price: $" + product.getCostPrice());
        System.out.println("Reorder Level: " + product.getReorderLevel());
        System.out.println("Created: " + product.getCreatedAt());
        
        // Show profitability
        try {
            ProductService.ProductProfitability profitability = 
                productService.calculateProfitability(productId);
            System.out.println("\nProfitability Analysis:");
            System.out.println("Profit Margin: " + profitability.getMarginPercentage() + "%");
            System.out.println("Category: " + profitability.getProfitabilityCategory());
        } catch (Exception e) {
            System.out.println("Could not calculate profitability: " + e.getMessage());
        }
    }
    
    // Category Management Methods
    private void listAllCategories() {
        List<Category> categories = categoryService.getAllCategories();
        System.out.println("\n--- All Categories ---");
        System.out.printf("%-5s %-20s %-40s%n", "ID", "Name", "Description");
        System.out.println("-".repeat(65));
        
        for (Category category : categories) {
            System.out.printf("%-5d %-20s %-40s%n",
                category.getCategoryId(),
                truncate(category.getName(), 19),
                truncate(category.getDescription(), 39)
            );
        }
    }
    
    private void addNewCategory() {
        System.out.println("\n--- Add New Category ---");
        
        try {
            String name = getStringInput("Category name: ");
            String description = getStringInput("Description: ");
            
            Category category = categoryService.createCategory(name, description);
            System.out.println("Category created successfully: " + category.getName() + 
                             " (ID: " + category.getCategoryId() + ")");
                             
        } catch (Exception e) {
            System.out.println("Error creating category: " + e.getMessage());
        }
    }
    
    private void updateCategory() {
        Long categoryId = getLongInput("Enter category ID to update: ");
        
        Optional<Category> categoryOpt = categoryService.getCategoryById(categoryId);
        if (categoryOpt.isEmpty()) {
            System.out.println("Category not found.");
            return;
        }
        
        Category category = categoryOpt.get();
        System.out.println("Current category: " + category.getName());
        
        try {
            String name = getStringInputOptional("New name [" + category.getName() + "]: ");
            String description = getStringInputOptional("New description [" + category.getDescription() + "]: ");
            
            if (name.isEmpty()) name = category.getName();
            if (description.isEmpty()) description = category.getDescription();
            
            Category updated = categoryService.updateCategory(categoryId, name, description);
            System.out.println("Category updated successfully: " + updated.getName());
            
        } catch (Exception e) {
            System.out.println("Error updating category: " + e.getMessage());
        }
    }
    
    private void viewCategoryStatistics() {
        Long categoryId = getLongInput("Enter category ID: ");
        
        try {
            CategoryService.CategoryStatistics stats = categoryService.getCategoryStatistics(categoryId);
            System.out.println("\n--- Category Statistics ---");
            System.out.println("Category: " + stats.getCategory().getName());
            System.out.println("Total Products: " + stats.getProductCount());
            System.out.println("Total Stock Value: " + "N/A"); // Placeholder since not calculated
            System.out.println("Average Unit Price: " + "N/A"); // Placeholder since not calculated
            
        } catch (Exception e) {
            System.out.println("Error getting category statistics: " + e.getMessage());
        }
    }
    
    // Inventory Management Methods
    private void showInventorySummary() {
        try {
            InventoryService.InventorySummary summary = inventoryService.getInventorySummary();
            System.out.println("\n--- Inventory Summary ---");
            System.out.println("Total Items: " + summary.totalItems());
            System.out.println("Total Stock Value: $" + summary.totalStockValue());
            System.out.println("Low Stock Items: " + summary.lowStockItems());
            System.out.println("Out of Stock Items: " + summary.outOfStockItems());
            System.out.println("Available Locations: " + summary.totalLocations());
            
        } catch (Exception e) {
            System.out.println("Error getting inventory summary: " + e.getMessage());
        }
    }
    
    private void checkStockLevels() {
        List<Inventory> inventory = inventoryService.getAllInventory();
        System.out.println("\n--- Current Stock Levels ---");
        System.out.printf("%-30s %-15s %-8s %-8s %-8s %-12s%n", 
            "Product", "SKU", "On Hand", "Allocated", "Available", "Status");
        System.out.println("-".repeat(90));
        
        for (Inventory item : inventory) {
            System.out.printf("%-30s %-15s %-8d %-8d %-8d %-12s%n",
                truncate(item.getProductName(), 29),
                item.getProductSku(),
                item.getQuantityOnHand(),
                item.getQuantityAllocated(),
                item.getQuantityAvailable(),
                item.getStockStatus()
            );
        }
    }
    
    private void receiveStock() {
        System.out.println("\n--- Receive Stock ---");
        
        try {
            Long productId = getLongInput("Product ID: ");
            String location = getStringInput("Location [MAIN]: ");
            if (location.isEmpty()) location = "MAIN";
            
            int quantity = getIntInput("Quantity to receive: ");
            String description = getStringInput("Description/Reference: ");
            String user = getStringInput("User [CLI-USER]: ");
            if (user.isEmpty()) user = "CLI-USER";
            
            inventoryService.receiveStock(productId, location, quantity, 
                ReferenceType.PURCHASE_ORDER, 1L, description, user);
                
            System.out.println("Stock received successfully!");
            
        } catch (Exception e) {
            System.out.println("Error receiving stock: " + e.getMessage());
        }
    }
    
    private void issueStock() {
        System.out.println("\n--- Issue Stock ---");
        
        try {
            Long productId = getLongInput("Product ID: ");
            String location = getStringInput("Location [MAIN]: ");
            if (location.isEmpty()) location = "MAIN";
            
            int quantity = getIntInput("Quantity to issue: ");
            String description = getStringInput("Description/Reference: ");
            String user = getStringInput("User [CLI-USER]: ");
            if (user.isEmpty()) user = "CLI-USER";
            
            inventoryService.issueStock(productId, location, quantity, 
                ReferenceType.SALES_ORDER, 1L, description, user);
                
            System.out.println("Stock issued successfully!");
            
        } catch (Exception e) {
            System.out.println("Error issuing stock: " + e.getMessage());
        }
    }
    
    private void adjustStock() {
        System.out.println("\n--- Adjust Stock ---");
        
        try {
            Long productId = getLongInput("Product ID: ");
            String location = getStringInput("Location [MAIN]: ");
            if (location.isEmpty()) location = "MAIN";
            
            int newQuantity = getIntInput("New stock quantity: ");
            String reason = getStringInput("Adjustment reason: ");
            String user = getStringInput("User [CLI-USER]: ");
            if (user.isEmpty()) user = "CLI-USER";
            
            inventoryService.adjustStock(productId, location, newQuantity, reason, user);
            System.out.println("Stock adjusted successfully!");
            
        } catch (Exception e) {
            System.out.println("Error adjusting stock: " + e.getMessage());
        }
    }
    
    private void allocateStock() {
        System.out.println("\n--- Allocate Stock ---");
        
        try {
            Long productId = getLongInput("Product ID: ");
            String location = getStringInput("Location [MAIN]: ");
            if (location.isEmpty()) location = "MAIN";
            
            int quantity = getIntInput("Quantity to allocate: ");
            String user = getStringInput("User [CLI-USER]: ");
            if (user.isEmpty()) user = "CLI-USER";
            
            boolean success = inventoryService.allocateStock(productId, location, quantity, user);
            if (success) {
                System.out.println("Stock allocated successfully!");
            } else {
                System.out.println("Failed to allocate stock (insufficient available stock).");
            }
            
        } catch (Exception e) {
            System.out.println("Error allocating stock: " + e.getMessage());
        }
    }
    
    private void viewStockMovements() {
        Long productId = getLongInput("Enter product ID (0 for all): ");
        
        try {
            List<StockMovement> movements;
            if (productId == 0) {
                movements = inventoryService.getAllStockMovements();
                System.out.println("\n--- All Stock Movements ---");
            } else {
                movements = inventoryService.getStockMovementHistory(productId);
                System.out.println("\n--- Stock Movements for Product " + productId + " ---");
            }
            
            System.out.printf("%-12s %-15s %-8s %-15s %-20s%n", 
                "Date", "Type", "Quantity", "User", "Description");
            System.out.println("-".repeat(70));
            
            for (StockMovement movement : movements) {
                System.out.printf("%-12s %-15s %-8d %-15s %-20s%n",
                    movement.getCreatedAt().toString().substring(0, 10),
                    movement.getMovementType(),
                    movement.getQuantity(),
                    truncate(movement.getCreatedBy(), 14),
                    truncate(movement.getMovementDescription(), 19)
                );
            }
            
        } catch (Exception e) {
            System.out.println("Error getting stock movements: " + e.getMessage());
        }
    }
    
    private void showLowStockReport() {
        List<Inventory> lowStockItems = inventoryService.getLowStockItems();
        
        if (lowStockItems.isEmpty()) {
            System.out.println("\nNo items are currently below reorder level.");
        } else {
            System.out.println("\n--- Low Stock Report ---");
            System.out.printf("%-30s %-15s %-8s %-8s %-12s%n", 
                "Product", "SKU", "On Hand", "Reorder", "Status");
            System.out.println("-".repeat(75));
            
            for (Inventory item : lowStockItems) {
                System.out.printf("%-30s %-15s %-8d %-8d %-12s%n",
                    truncate(item.getProductName(), 29),
                    item.getProductSku(),
                    item.getQuantityOnHand(),
                    item.getReorderLevel(),
                    item.getStockStatus()
                );
            }
        }
    }
    
    // Report Methods
    private void showInventoryValuation() {
        List<Inventory> inventory = inventoryService.getAllInventory();
        
        System.out.println("\n--- Inventory Valuation Report ---");
        System.out.printf("%-30s %-8s %-10s %-12s%n", 
            "Product", "Quantity", "Unit Price", "Total Value");
        System.out.println("-".repeat(60));
        
        BigDecimal totalValue = BigDecimal.ZERO;
        for (Inventory item : inventory) {
            // Get product details for pricing
            Optional<Product> productOpt = productService.getProductById(item.getProductId());
            if (productOpt.isPresent()) {
                Product product = productOpt.get();
                BigDecimal itemValue = product.getUnitPrice()
                    .multiply(BigDecimal.valueOf(item.getQuantityOnHand()));
                totalValue = totalValue.add(itemValue);
                
                System.out.printf("%-30s %-8d $%-9.2f $%-11.2f%n",
                    truncate(item.getProductName(), 29),
                    item.getQuantityOnHand(),
                    product.getUnitPrice(),
                    itemValue
                );
            }
        }
        
        System.out.println("-".repeat(60));
        System.out.printf("%41s $%-11.2f%n", "Total Inventory Value:", totalValue);
    }
    
    private void showLowStockItems() {
        showLowStockReport();
    }
    
    private void showOutOfStockItems() {
        List<Inventory> outOfStockItems = inventoryService.getOutOfStockItems();
        
        if (outOfStockItems.isEmpty()) {
            System.out.println("\nNo items are currently out of stock.");
        } else {
            System.out.println("\n--- Out of Stock Report ---");
            System.out.printf("%-30s %-15s %-12s%n", "Product", "SKU", "Status");
            System.out.println("-".repeat(57));
            
            for (Inventory item : outOfStockItems) {
                System.out.printf("%-30s %-15s %-12s%n",
                    truncate(item.getProductName(), 29),
                    item.getProductSku(),
                    item.getStockStatus()
                );
            }
        }
    }
    
    private void showCategorySummary() {
        List<Category> categories = categoryService.getAllCategories();
        
        System.out.println("\n--- Category Summary Report ---");
        System.out.printf("%-20s %-10s %-15s %-15s%n", 
            "Category", "Products", "Stock Value", "Avg Price");
        System.out.println("-".repeat(60));
        
        for (Category category : categories) {
            try {
                CategoryService.CategoryStatistics stats = 
                    categoryService.getCategoryStatistics(category.getCategoryId());
                    
                System.out.printf("%-20s %-10d %-15s %-15s%n",
                    truncate(category.getName(), 19),
                    stats.getProductCount(),
                    "N/A", // Placeholder since not calculated in current stats
                    "N/A"  // Placeholder since not calculated in current stats
                );
            } catch (Exception e) {
                System.out.printf("%-20s %-10s %-15s %-15s%n",
                    truncate(category.getName(), 19), "Error", "Error", "Error");
            }
        }
    }
    
    private void showStockMovementHistory() {
        viewStockMovements();
    }
    
    private void showSystemInfo() {
        try {
            DatabaseConfig dbConfig = DatabaseConfig.getInstance();
            System.out.println("\n--- System Information ---");
            System.out.println("Database Type: " + dbConfig.getDatabaseType());
            System.out.println("Connection Pool Stats: " + dbConfig.getPoolStats());
            
            List<String> locations = inventoryService.getAllLocations();
            System.out.println("Available Locations: " + String.join(", ", locations));
            
            // Get summary statistics
            InventoryService.InventorySummary summary = inventoryService.getInventorySummary();
            System.out.println("\nQuick Stats:");
            System.out.println("- Total Items: " + summary.totalItems());
            System.out.println("- Total Categories: " + categoryService.getAllCategories().size());
            System.out.println("- Total Products: " + productService.getAllProducts().size());
            System.out.println("- Low Stock Items: " + summary.lowStockItems());
            
        } catch (Exception e) {
            System.out.println("Error getting system information: " + e.getMessage());
        }
    }
    
    // Utility Methods
    private String getStringInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }
    
    private String getStringInputOptional(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }
    
    private int getIntInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }
    
    private Long getLongInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Long.parseLong(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }
    
    private BigDecimal getBigDecimalInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return new BigDecimal(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid decimal number.");
            }
        }
    }
    
    private String truncate(String str, int length) {
        if (str == null) return "";
        if (str.length() <= length) return str;
        return str.substring(0, length - 3) + "...";
    }
}