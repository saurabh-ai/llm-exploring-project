package com.javamastery.inventory.integration;

import com.javamastery.inventory.config.DatabaseConfig;
import com.javamastery.inventory.config.DatabaseInitializer;
import com.javamastery.inventory.model.*;
import com.javamastery.inventory.service.*;
import com.javamastery.inventory.util.ReportGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test demonstrating complete workflow functionality
 */
class InventoryManagementIntegrationTest {

    private CategoryService categoryService;
    private ProductService productService;
    private InventoryService inventoryService;
    private PurchaseOrderService purchaseOrderService;
    private ReportGenerator reportGenerator;

    @BeforeEach
    void setUp() throws Exception {
        // Initialize with fresh H2 in-memory database
        DatabaseConfig dbConfig = DatabaseConfig.getInstance();
        DataSource dataSource = dbConfig.getDataSource();
        
        DatabaseInitializer initializer = new DatabaseInitializer(dataSource, dbConfig.getDatabaseType());
        initializer.dropAllTables();
        initializer.initializeWithSampleData();
        
        // Initialize services
        categoryService = new CategoryService();
        productService = new ProductService();
        inventoryService = new InventoryService();
        purchaseOrderService = new PurchaseOrderService();
        reportGenerator = new ReportGenerator();
    }

    @Test
    void testCompleteInventoryWorkflow() {
        // 1. Create a new category
        Category testCategory = categoryService.createCategory("Test Electronics", "Test electronic devices");
        assertNotNull(testCategory);
        assertEquals("Test Electronics", testCategory.getName());

        // 2. Create a new product in this category
        Product testProduct = productService.createProduct(
            "Test Smartphone",
            "High-end test smartphone",
            "TEST",  // SKU prefix
            testCategory.getCategoryId(),
            new BigDecimal("699.99"),
            new BigDecimal("400.00"),
            15  // reorder level
        );
        assertNotNull(testProduct);
        assertTrue(testProduct.getSku().startsWith("TEST-"));

        // 3. Check initial inventory (should be 0)
        Optional<Inventory> initialInventory = inventoryService.getInventoryByProduct(testProduct.getProductId());
        if (initialInventory.isPresent()) {
            assertEquals(0, initialInventory.get().getQuantityOnHand());
        }

        // 4. Receive stock
        inventoryService.receiveStock(
            testProduct.getProductId(),
            "MAIN",
            100,
            ReferenceType.PURCHASE_ORDER,
            1L,
            "Initial stock receipt",
            "IntegrationTest"
        );

        // 5. Verify stock was received
        Optional<Inventory> afterReceipt = inventoryService.getInventoryByProduct(testProduct.getProductId());
        assertTrue(afterReceipt.isPresent());
        assertEquals(100, afterReceipt.get().getQuantityOnHand());

        // 6. Allocate some stock
        boolean allocated = inventoryService.allocateStock(
            testProduct.getProductId(),
            "MAIN",
            20,
            "IntegrationTest"
        );
        assertTrue(allocated);

        // 7. Verify allocation
        Optional<Inventory> afterAllocation = inventoryService.getInventoryByProduct(testProduct.getProductId());
        assertTrue(afterAllocation.isPresent());
        assertEquals(100, afterAllocation.get().getQuantityOnHand());
        assertEquals(20, afterAllocation.get().getQuantityAllocated());
        assertEquals(80, afterAllocation.get().getQuantityAvailable());

        // 8. Issue some stock
        inventoryService.issueStock(
            testProduct.getProductId(),
            "MAIN",
            10,
            ReferenceType.SALES_ORDER,
            1L,
            "Test sale",
            "IntegrationTest"
        );

        // 9. Verify stock was issued
        Optional<Inventory> afterIssue = inventoryService.getInventoryByProduct(testProduct.getProductId());
        assertTrue(afterIssue.isPresent());
        assertEquals(90, afterIssue.get().getQuantityOnHand());
        assertEquals(20, afterIssue.get().getQuantityAllocated());
        assertEquals(70, afterIssue.get().getQuantityAvailable());

        // 10. Check stock movement history
        List<StockMovement> movements = inventoryService.getStockMovementHistory(testProduct.getProductId());
        assertFalse(movements.isEmpty());
        // Should have: receive, allocate, issue movements
        assertTrue(movements.size() >= 3);

        // 11. Test reporting
        InventoryService.InventorySummary summary = inventoryService.getInventorySummary();
        assertTrue(summary.totalItems() > 0);

        // 12. Test low stock detection by adjusting stock below reorder level
        inventoryService.adjustStock(
            testProduct.getProductId(),
            "MAIN",
            10,  // Below reorder level of 15
            "Test low stock scenario",
            "IntegrationTest"
        );

        List<Inventory> lowStockItems = inventoryService.getLowStockItems();
        boolean foundLowStockItem = lowStockItems.stream()
            .anyMatch(item -> item.getProductId().equals(testProduct.getProductId()));
        assertTrue(foundLowStockItem);

        // 13. Test purchase order workflow
        PurchaseOrder testPO = purchaseOrderService.createPurchaseOrder(
            1L,  // Assuming supplier ID 1 exists from sample data
            "Test purchase order for integration test"
        );
        assertNotNull(testPO);
        assertEquals(PurchaseOrderStatus.DRAFT, testPO.getStatus());

        // Update PO status through workflow
        PurchaseOrder sentPO = purchaseOrderService.updateStatus(testPO.getPoId(), PurchaseOrderStatus.SENT);
        assertEquals(PurchaseOrderStatus.SENT, sentPO.getStatus());

        PurchaseOrder confirmedPO = purchaseOrderService.updateStatus(testPO.getPoId(), PurchaseOrderStatus.CONFIRMED);
        assertEquals(PurchaseOrderStatus.CONFIRMED, confirmedPO.getStatus());

        PurchaseOrder receivedPO = purchaseOrderService.completePurchaseOrder(testPO.getPoId());
        assertEquals(PurchaseOrderStatus.RECEIVED, receivedPO.getStatus());
    }

    @Test
    void testReportGeneration() throws Exception {
        // Test CSV report generation
        String csvReport = reportGenerator.generateInventoryValuationCSV();
        assertNotNull(csvReport);
        assertTrue(csvReport.contains("Product Name"));
        assertTrue(csvReport.contains("Total Inventory Value"));

        // Test JSON report generation
        String jsonReport = reportGenerator.generateInventoryValuationJSON();
        assertNotNull(jsonReport);
        assertTrue(jsonReport.contains("reportDate"));
        assertTrue(jsonReport.contains("totalValue"));

        // Test low stock report
        String lowStockCSV = reportGenerator.generateLowStockCSV();
        assertNotNull(lowStockCSV);
        assertTrue(lowStockCSV.contains("Product Name"));

        // Test dashboard JSON
        String dashboardJSON = reportGenerator.generateDashboardJSON();
        assertNotNull(dashboardJSON);
        assertTrue(dashboardJSON.contains("summary"));
        assertTrue(dashboardJSON.contains("alerts"));
    }

    @Test
    void testDatabaseIntegrity() {
        // Test that all expected sample data exists
        List<Category> categories = categoryService.getAllCategories();
        assertFalse(categories.isEmpty());

        List<Product> products = productService.getAllProducts();
        assertFalse(products.isEmpty());

        List<Inventory> inventory = inventoryService.getAllInventory();
        assertFalse(inventory.isEmpty());

        // Test relationships are maintained
        for (Product product : products) {
            assertNotNull(product.getCategoryId());
            assertNotNull(product.getCategoryName());
        }

        for (Inventory item : inventory) {
            assertNotNull(item.getProductId());
            assertNotNull(item.getProductName());
            assertNotNull(item.getProductSku());
        }
    }

    @Test
    void testBusinessRuleValidation() {
        // Test that business rules are enforced

        // 1. Cannot create duplicate category names
        categoryService.createCategory("Unique Category", "Description");
        assertThrows(Exception.class, () -> 
            categoryService.createCategory("Unique Category", "Different description"));

        // 2. Cannot create products without valid category
        assertThrows(Exception.class, () -> 
            productService.createProduct("Invalid Product", "Description", "INV", 9999L, 
                new BigDecimal("10.00"), new BigDecimal("5.00"), 10));

        // 3. Cannot allocate more stock than available
        Optional<Inventory> firstItem = inventoryService.getAllInventory().stream().findFirst();
        if (firstItem.isPresent()) {
            Inventory item = firstItem.get();
            assertFalse(inventoryService.allocateStock(
                item.getProductId(),
                item.getLocation(),
                item.getQuantityAvailable() + 1000,  // More than available
                "Test"
            ));
        }
    }
}