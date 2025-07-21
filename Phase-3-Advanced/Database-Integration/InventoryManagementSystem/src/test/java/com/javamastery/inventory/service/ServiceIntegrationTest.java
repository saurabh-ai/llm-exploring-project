package com.javamastery.inventory.service;

import com.javamastery.inventory.config.DatabaseConfig;
import com.javamastery.inventory.config.DatabaseInitializer;
import com.javamastery.inventory.exception.DatabaseException;
import com.javamastery.inventory.exception.ValidationException;
import com.javamastery.inventory.model.*;
import org.junit.jupiter.api.*;

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test for UserService and InventoryTransactionService
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ServiceIntegrationTest {
    
    private static DataSource dataSource;
    private static UserService userService;
    private static InventoryTransactionService transactionService;
    
    @BeforeAll
    static void setUp() throws Exception {
        // Configure test database with unique name
        System.setProperty("database.url", "jdbc:h2:mem:integrationtest" + System.currentTimeMillis() + ";DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE");
        
        DatabaseConfig dbConfig = DatabaseConfig.getInstance();
        dataSource = dbConfig.getDataSource();
        userService = new UserService(dataSource);
        transactionService = new InventoryTransactionService(dataSource);
        
        // Initialize database with schema and sample data
        DatabaseInitializer initializer = new DatabaseInitializer(dataSource, dbConfig.getDatabaseType());
        initializer.initializeWithSampleData();
    }
    
    @Test
    @Order(1)
    void testUserServiceBasicOperations() throws DatabaseException, ValidationException {
        // Test getting all users
        List<User> users = userService.getAllUsers();
        assertTrue(users.size() >= 4);
        
        // Test creating a new user
        User newUser = userService.createUser("testmanager", "testmanager@inventory.com", UserRole.MANAGER);
        assertNotNull(newUser.getUserId());
        assertEquals("testmanager", newUser.getUsername());
        assertEquals(UserRole.MANAGER, newUser.getRole());
        
        // Test finding by username
        Optional<User> found = userService.getUserByUsername("testmanager");
        assertTrue(found.isPresent());
        assertEquals(newUser.getUserId(), found.get().getUserId());
        
        // Test getting users by role
        List<User> managers = userService.getUsersByRole(UserRole.MANAGER);
        assertTrue(managers.size() >= 2); // original + new one
    }
    
    @Test
    @Order(2)
    void testUserServiceValidation() {
        // Test creating user with invalid data
        assertThrows(ValidationException.class, () -> {
            userService.createUser("", "test@example.com", UserRole.EMPLOYEE);
        });
        
        assertThrows(ValidationException.class, () -> {
            userService.createUser("validuser", "invalid-email", UserRole.EMPLOYEE);
        });
        
        assertThrows(ValidationException.class, () -> {
            userService.createUser("validuser", "valid@example.com", null);
        });
        
        // Test creating duplicate username
        assertThrows(ValidationException.class, () -> {
            userService.createUser("admin", "new@example.com", UserRole.EMPLOYEE);
        });
    }
    
    @Test
    @Order(3)
    void testTransactionServiceBasicOperations() throws DatabaseException, ValidationException {
        // Get admin user for transactions
        Optional<User> admin = userService.getUserByUsername("admin");
        assertTrue(admin.isPresent());
        
        // Create stock in transaction
        InventoryTransaction stockIn = transactionService.recordStockIn(
            1L, 50, "Purchase order received", admin.get().getUserId()
        );
        assertNotNull(stockIn.getTransactionId());
        assertEquals(TransactionType.IN, stockIn.getTransactionType());
        assertEquals(50, stockIn.getQuantity());
        
        // Create stock out transaction
        InventoryTransaction stockOut = transactionService.recordStockOut(
            1L, 20, "Sales order fulfilled", admin.get().getUserId()
        );
        assertNotNull(stockOut.getTransactionId());
        assertEquals(TransactionType.OUT, stockOut.getTransactionType());
        assertEquals(20, stockOut.getQuantity());
        
        // Test getting transactions by product
        List<InventoryTransaction> productTransactions = transactionService.getTransactionsByProduct(1L);
        assertTrue(productTransactions.size() >= 2);
        
        // Test getting transactions by user
        List<InventoryTransaction> userTransactions = transactionService.getTransactionsByUser(admin.get().getUserId());
        assertTrue(userTransactions.size() >= 2);
    }
    
    @Test
    @Order(4)
    void testTransactionServiceValidation() throws DatabaseException {
        // Get admin user for transactions
        Optional<User> admin = userService.getUserByUsername("admin");
        assertTrue(admin.isPresent());
        
        // Test invalid product ID
        assertThrows(ValidationException.class, () -> {
            transactionService.createTransaction(
                999L, TransactionType.IN, 10, "Test", admin.get().getUserId()
            );
        });
        
        // Test invalid user ID
        assertThrows(ValidationException.class, () -> {
            transactionService.createTransaction(
                1L, TransactionType.IN, 10, "Test", 999L
            );
        });
        
        // Test invalid quantity
        assertThrows(ValidationException.class, () -> {
            transactionService.createTransaction(
                1L, TransactionType.IN, -5, "Test", admin.get().getUserId()
            );
        });
        
        // Test missing reason
        assertThrows(ValidationException.class, () -> {
            transactionService.createTransaction(
                1L, TransactionType.IN, 10, "", admin.get().getUserId()
            );
        });
    }
    
    @Test
    @Order(5)
    void testTransactionSummary() throws DatabaseException, ValidationException {
        // Get transaction summary for product 1
        InventoryTransactionService.TransactionSummary summary = 
            transactionService.getProductTransactionSummary(1L);
        
        assertNotNull(summary);
        assertEquals(1L, summary.getProductId());
        assertTrue(summary.getTotalStockIn() > 0);
        assertTrue(summary.getTotalStockOut() >= 0);
        assertTrue(summary.getTransactionCount() > 0);
        
        // Net stock should be positive
        assertTrue(summary.getNetStock() > 0);
    }
    
    @Test
    @Order(6)
    void testDateRangeFiltering() throws DatabaseException, ValidationException {
        LocalDateTime yesterday = LocalDateTime.now().minusDays(1);
        LocalDateTime tomorrow = LocalDateTime.now().plusDays(1);
        
        // Get transactions within date range
        List<InventoryTransaction> transactions = transactionService.getTransactionsByDateRange(yesterday, tomorrow);
        
        // Should include all transactions created in our tests
        assertTrue(transactions.size() > 0);
        
        // Test invalid date range
        assertThrows(ValidationException.class, () -> {
            transactionService.getTransactionsByDateRange(tomorrow, yesterday);
        });
    }
    
    @Test
    @Order(7)
    void testUserDeletion() throws DatabaseException, ValidationException {
        // Create a test user to delete
        User testUser = userService.createUser("deletetest", "delete@test.com", UserRole.EMPLOYEE);
        Long userIdToDelete = testUser.getUserId();
        
        // Verify user exists
        assertTrue(userService.getUserById(userIdToDelete).isPresent());
        
        // Delete the user
        boolean deleted = userService.deleteUser(userIdToDelete);
        assertTrue(deleted);
        
        // Verify user no longer exists
        assertFalse(userService.getUserById(userIdToDelete).isPresent());
        
        // Test deleting the last admin (should fail)
        Optional<User> admin = userService.getUserByUsername("admin");
        assertTrue(admin.isPresent());
        
        assertThrows(ValidationException.class, () -> {
            userService.deleteUser(admin.get().getUserId());
        });
    }
    
    @Test
    @Order(8)
    void testConcurrentTransactions() throws DatabaseException, ValidationException {
        // Get admin user
        Optional<User> admin = userService.getUserByUsername("admin");
        assertTrue(admin.isPresent());
        
        // Create multiple transactions simultaneously (simulating concurrent access)
        InventoryTransaction tx1 = transactionService.recordStockIn(
            2L, 25, "Concurrent transaction 1", admin.get().getUserId()
        );
        
        InventoryTransaction tx2 = transactionService.recordStockOut(
            2L, 10, "Concurrent transaction 2", admin.get().getUserId()
        );
        
        InventoryTransaction tx3 = transactionService.recordStockIn(
            2L, 15, "Concurrent transaction 3", admin.get().getUserId()
        );
        
        // Verify all transactions were created
        assertNotNull(tx1.getTransactionId());
        assertNotNull(tx2.getTransactionId());
        assertNotNull(tx3.getTransactionId());
        
        // Verify they have different IDs
        assertNotEquals(tx1.getTransactionId(), tx2.getTransactionId());
        assertNotEquals(tx2.getTransactionId(), tx3.getTransactionId());
        assertNotEquals(tx1.getTransactionId(), tx3.getTransactionId());
        
        // Verify summary is accurate
        InventoryTransactionService.TransactionSummary summary = 
            transactionService.getProductTransactionSummary(2L);
        assertEquals(40, summary.getTotalStockIn());
        assertEquals(10, summary.getTotalStockOut());
        assertEquals(30, summary.getNetStock());
    }
    
    @Test
    @Order(9)
    void testCompleteWorkflow() throws DatabaseException, ValidationException {
        // Create a new employee user
        User employee = userService.createUser("warehouseuser", "warehouse@inventory.com", UserRole.EMPLOYEE);
        
        // Employee receives inventory
        InventoryTransaction receiving = transactionService.recordStockIn(
            3L, 100, "Warehouse receiving", employee.getUserId()
        );
        
        // Employee processes outbound shipment
        InventoryTransaction shipment = transactionService.recordStockOut(
            3L, 30, "Customer shipment", employee.getUserId()
        );
        
        // Get employee's transaction history
        List<InventoryTransaction> employeeTransactions = 
            transactionService.getTransactionsByUser(employee.getUserId());
        
        assertEquals(2, employeeTransactions.size());
        
        // Verify the transactions are properly linked to the employee
        for (InventoryTransaction transaction : employeeTransactions) {
            assertEquals(employee.getUserId(), transaction.getUserId());
            assertEquals("warehouseuser", transaction.getUsername());
        }
        
        // Get product transaction summary
        InventoryTransactionService.TransactionSummary summary = 
            transactionService.getProductTransactionSummary(3L);
        
        assertTrue(summary.getTotalStockIn() >= 100);
        assertTrue(summary.getTotalStockOut() >= 30);
        assertTrue(summary.getNetStock() >= 70);
    }
    
    @AfterAll
    static void tearDown() {
        // Clean up if needed
        if (dataSource != null) {
            try {
                dataSource.getConnection().close();
            } catch (Exception e) {
                // Ignore cleanup errors
            }
        }
    }
}