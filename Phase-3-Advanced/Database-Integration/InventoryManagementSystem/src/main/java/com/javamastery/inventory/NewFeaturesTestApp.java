package com.javamastery.inventory;

import com.javamastery.inventory.config.DatabaseConfig;
import com.javamastery.inventory.config.DatabaseInitializer;
import com.javamastery.inventory.dao.InventoryTransactionDao;
import com.javamastery.inventory.dao.UserDao;
import com.javamastery.inventory.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;

/**
 * Test application to demonstrate new User and InventoryTransaction functionality
 */
public class NewFeaturesTestApp {
    private static final Logger logger = LoggerFactory.getLogger(NewFeaturesTestApp.class);
    
    public static void main(String[] args) {
        try {
            logger.info("Starting New Features Test Application");
            
            // Initialize database
            DatabaseConfig dbConfig = DatabaseConfig.getInstance();
            DataSource dataSource = dbConfig.getDataSource();
            
            // Initialize database if needed
            DatabaseInitializer initializer = new DatabaseInitializer(dataSource, dbConfig.getDatabaseType());
            if (!initializer.schemaExists()) {
                logger.info("Database schema not found, initializing...");
                initializer.initializeWithSampleData();
            }
            
            // Test UserDao
            logger.info("Testing UserDao...");
            UserDao userDao = new UserDao(dataSource);
            
            List<User> users = userDao.findAll();
            logger.info("Found {} users", users.size());
            
            for (User user : users) {
                logger.info("User: {} ({}), Role: {}", user.getUsername(), user.getEmail(), user.getRole());
            }
            
            // Find users by role
            List<User> admins = userDao.findByRole(UserRole.ADMIN);
            logger.info("Found {} administrators", admins.size());
            
            // Test InventoryTransactionDao
            logger.info("Testing InventoryTransactionDao...");
            InventoryTransactionDao transactionDao = new InventoryTransactionDao(dataSource);
            
            List<InventoryTransaction> transactions = transactionDao.findAll();
            logger.info("Found {} inventory transactions", transactions.size());
            
            for (InventoryTransaction transaction : transactions) {
                logger.info("Transaction: {} {} units of {} by {} - {}", 
                    transaction.getTransactionType(),
                    transaction.getQuantity(),
                    transaction.getProductName(),
                    transaction.getUsername(),
                    transaction.getReason()
                );
            }
            
            // Test creating new transaction
            Optional<User> admin = userDao.findByUsername("admin");
            if (admin.isPresent()) {
                InventoryTransaction newTransaction = new InventoryTransaction(
                    1L, // product_id
                    TransactionType.OUT,
                    3,
                    "Test sale transaction",
                    admin.get().getUserId()
                );
                
                InventoryTransaction saved = transactionDao.save(newTransaction);
                logger.info("Created new transaction with ID: {}", saved.getTransactionId());
            }
            
            // Test filtering by product
            List<InventoryTransaction> productTransactions = transactionDao.findByProductId(1L);
            logger.info("Found {} transactions for product 1", productTransactions.size());
            
            logger.info("New Features Test Application completed successfully");
            
        } catch (Exception e) {
            logger.error("Error in New Features Test Application", e);
            System.exit(1);
        }
    }
}