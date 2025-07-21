package com.javamastery.inventory.dao;

import com.javamastery.inventory.config.DatabaseConfig;
import com.javamastery.inventory.config.DatabaseInitializer;
import com.javamastery.inventory.exception.DatabaseException;
import com.javamastery.inventory.model.InventoryTransaction;
import com.javamastery.inventory.model.TransactionType;
import org.junit.jupiter.api.*;

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for InventoryTransactionDao
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class InventoryTransactionDaoTest {
    
    private static DataSource dataSource;
    private static InventoryTransactionDao transactionDao;
    
    @BeforeAll
    static void setUp() throws Exception {
        // Configure test database with unique name
        System.setProperty("database.url", "jdbc:h2:mem:transactiontest" + System.currentTimeMillis() + ";DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE");
        
        DatabaseConfig dbConfig = DatabaseConfig.getInstance();
        dataSource = dbConfig.getDataSource();
        transactionDao = new InventoryTransactionDao(dataSource);
        
        // Initialize database with schema and sample data
        DatabaseInitializer initializer = new DatabaseInitializer(dataSource, dbConfig.getDatabaseType());
        initializer.initializeWithSampleData();
    }
    
    @Test
    @Order(1)
    void testFindAll() throws DatabaseException {
        List<InventoryTransaction> transactions = transactionDao.findAll();
        
        assertNotNull(transactions);
        assertTrue(transactions.size() >= 9); // Should have at least the sample transactions
        
        // Check that transactions include joined data
        for (InventoryTransaction transaction : transactions) {
            assertNotNull(transaction.getProductName());
            assertNotNull(transaction.getUsername());
        }
        
        // Verify they are ordered by transaction_date DESC
        for (int i = 1; i < transactions.size(); i++) {
            assertTrue(
                transactions.get(i-1).getTransactionDate().isAfter(transactions.get(i).getTransactionDate()) ||
                transactions.get(i-1).getTransactionDate().equals(transactions.get(i).getTransactionDate())
            );
        }
    }
    
    @Test
    @Order(2)
    void testFindById() throws DatabaseException {
        // Find existing transaction
        Optional<InventoryTransaction> transaction = transactionDao.findById(1L);
        
        assertTrue(transaction.isPresent());
        assertEquals(1L, transaction.get().getProductId());
        assertEquals(TransactionType.IN, transaction.get().getTransactionType());
        assertEquals(20, transaction.get().getQuantity());
        assertEquals("Initial stock purchase", transaction.get().getReason());
        assertNotNull(transaction.get().getProductName());
        assertNotNull(transaction.get().getUsername());
        
        // Find non-existing transaction
        Optional<InventoryTransaction> nonExisting = transactionDao.findById(999L);
        assertFalse(nonExisting.isPresent());
    }
    
    @Test
    @Order(3)
    void testFindByProductId() throws DatabaseException {
        // Find transactions for product ID 1
        List<InventoryTransaction> transactions = transactionDao.findByProductId(1L);
        
        assertNotNull(transactions);
        assertTrue(transactions.size() >= 2); // Should have at least 2 transactions for product 1
        
        // Verify all transactions are for product 1
        for (InventoryTransaction transaction : transactions) {
            assertEquals(1L, transaction.getProductId());
            assertNotNull(transaction.getProductName());
        }
        
        // Find transactions for non-existing product
        List<InventoryTransaction> nonExisting = transactionDao.findByProductId(999L);
        assertTrue(nonExisting.isEmpty());
    }
    
    @Test
    @Order(4)
    void testFindByUserId() throws DatabaseException {
        // Find transactions for user ID 1 (admin)
        List<InventoryTransaction> transactions = transactionDao.findByUserId(1L);
        
        assertNotNull(transactions);
        assertTrue(transactions.size() >= 1);
        
        // Verify all transactions are by user 1
        for (InventoryTransaction transaction : transactions) {
            assertEquals(1L, transaction.getUserId());
            assertEquals("admin", transaction.getUsername());
        }
        
        // Find transactions for non-existing user
        List<InventoryTransaction> nonExisting = transactionDao.findByUserId(999L);
        assertTrue(nonExisting.isEmpty());
    }
    
    @Test
    @Order(5)
    void testFindByDateRange() throws DatabaseException {
        LocalDateTime startDate = LocalDateTime.now().minusDays(1);
        LocalDateTime endDate = LocalDateTime.now().plusDays(1);
        
        List<InventoryTransaction> transactions = transactionDao.findByDateRange(startDate, endDate);
        
        assertNotNull(transactions);
        assertTrue(transactions.size() >= 9); // Should include today's transactions
        
        // Verify all transactions are within the date range
        for (InventoryTransaction transaction : transactions) {
            assertTrue(
                !transaction.getTransactionDate().isBefore(startDate) &&
                !transaction.getTransactionDate().isAfter(endDate)
            );
        }
    }
    
    @Test
    @Order(6)
    void testSaveNewTransaction() throws DatabaseException {
        InventoryTransaction newTransaction = new InventoryTransaction(
            1L, // product_id
            TransactionType.OUT,
            5,
            "Test transaction",
            2L // user_id
        );
        
        InventoryTransaction savedTransaction = transactionDao.save(newTransaction);
        
        assertNotNull(savedTransaction.getTransactionId());
        assertEquals(1L, savedTransaction.getProductId());
        assertEquals(TransactionType.OUT, savedTransaction.getTransactionType());
        assertEquals(5, savedTransaction.getQuantity());
        assertEquals("Test transaction", savedTransaction.getReason());
        assertEquals(2L, savedTransaction.getUserId());
        assertNotNull(savedTransaction.getTransactionDate());
        assertNotNull(savedTransaction.getProductName());
        assertNotNull(savedTransaction.getUsername());
    }
    
    @Test
    @Order(7)
    void testSaveExistingTransaction() throws DatabaseException {
        // First, get a transaction to update
        List<InventoryTransaction> transactions = transactionDao.findByProductId(1L);
        assertFalse(transactions.isEmpty());
        
        InventoryTransaction transaction = transactions.get(0);
        transaction.setReason("Updated reason");
        transaction.setQuantity(15);
        
        InventoryTransaction updatedTransaction = transactionDao.save(transaction);
        
        assertEquals(transaction.getTransactionId(), updatedTransaction.getTransactionId());
        assertEquals("Updated reason", updatedTransaction.getReason());
        assertEquals(15, updatedTransaction.getQuantity());
    }
    
    @Test
    @Order(8)
    void testDeleteById() throws DatabaseException {
        // First, create a transaction to delete
        InventoryTransaction transactionToDelete = new InventoryTransaction(
            2L, TransactionType.IN, 10, "Delete test", 3L
        );
        InventoryTransaction savedTransaction = transactionDao.save(transactionToDelete);
        Long transactionIdToDelete = savedTransaction.getTransactionId();
        
        // Verify transaction exists
        assertTrue(transactionDao.findById(transactionIdToDelete).isPresent());
        
        // Delete transaction
        boolean deleted = transactionDao.deleteById(transactionIdToDelete);
        assertTrue(deleted);
        
        // Verify transaction no longer exists
        assertFalse(transactionDao.findById(transactionIdToDelete).isPresent());
        
        // Try to delete non-existing transaction
        boolean nonExistingDeleted = transactionDao.deleteById(999L);
        assertFalse(nonExistingDeleted);
    }
    
    @Test
    @Order(9)
    void testForeignKeyConstraints() {
        // Test invalid product_id
        assertThrows(DatabaseException.class, () -> {
            InventoryTransaction invalidProduct = new InventoryTransaction(
                999L, TransactionType.IN, 10, "Invalid product", 1L
            );
            transactionDao.save(invalidProduct);
        });
        
        // Test invalid user_id
        assertThrows(DatabaseException.class, () -> {
            InventoryTransaction invalidUser = new InventoryTransaction(
                1L, TransactionType.IN, 10, "Invalid user", 999L
            );
            transactionDao.save(invalidUser);
        });
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