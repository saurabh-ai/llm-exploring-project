package com.javamastery.inventory.service;

import com.javamastery.inventory.dao.InventoryTransactionDao;
import com.javamastery.inventory.dao.ProductDao;
import com.javamastery.inventory.dao.UserDao;
import com.javamastery.inventory.exception.DatabaseException;
import com.javamastery.inventory.exception.ValidationException;
import com.javamastery.inventory.model.InventoryTransaction;
import com.javamastery.inventory.model.Product;
import com.javamastery.inventory.model.TransactionType;
import com.javamastery.inventory.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service class for InventoryTransaction business logic and operations
 */
public class InventoryTransactionService {
    private static final Logger logger = LoggerFactory.getLogger(InventoryTransactionService.class);
    
    private final InventoryTransactionDao transactionDao;
    private final ProductDao productDao;
    private final UserDao userDao;
    
    public InventoryTransactionService(DataSource dataSource) {
        this.transactionDao = new InventoryTransactionDao(dataSource);
        this.productDao = new ProductDao(dataSource);
        this.userDao = new UserDao(dataSource);
    }
    
    /**
     * Get all transactions
     */
    public List<InventoryTransaction> getAllTransactions() throws DatabaseException {
        logger.debug("Getting all inventory transactions");
        return transactionDao.findAll();
    }
    
    /**
     * Get transaction by ID
     */
    public Optional<InventoryTransaction> getTransactionById(Long transactionId) throws DatabaseException {
        logger.debug("Getting transaction by ID: {}", transactionId);
        return transactionDao.findById(transactionId);
    }
    
    /**
     * Get transactions by product
     */
    public List<InventoryTransaction> getTransactionsByProduct(Long productId) throws DatabaseException {
        logger.debug("Getting transactions for product: {}", productId);
        return transactionDao.findByProductId(productId);
    }
    
    /**
     * Get transactions by user
     */
    public List<InventoryTransaction> getTransactionsByUser(Long userId) throws DatabaseException {
        logger.debug("Getting transactions for user: {}", userId);
        return transactionDao.findByUserId(userId);
    }
    
    /**
     * Get transactions within date range
     */
    public List<InventoryTransaction> getTransactionsByDateRange(LocalDateTime startDate, LocalDateTime endDate) 
            throws DatabaseException, ValidationException {
        logger.debug("Getting transactions between {} and {}", startDate, endDate);
        
        if (startDate.isAfter(endDate)) {
            throw new ValidationException("Start date cannot be after end date");
        }
        
        return transactionDao.findByDateRange(startDate, endDate);
    }
    
    /**
     * Create new transaction (stock in)
     */
    public InventoryTransaction recordStockIn(Long productId, Integer quantity, String reason, Long userId) 
            throws DatabaseException, ValidationException {
        logger.info("Recording stock in: {} units of product {} by user {}", quantity, productId, userId);
        
        return createTransaction(productId, TransactionType.IN, quantity, reason, userId);
    }
    
    /**
     * Create new transaction (stock out)
     */
    public InventoryTransaction recordStockOut(Long productId, Integer quantity, String reason, Long userId) 
            throws DatabaseException, ValidationException {
        logger.info("Recording stock out: {} units of product {} by user {}", quantity, productId, userId);
        
        // For now, we'll just create the transaction without stock validation
        // In a production system, you would check current inventory levels here
        return createTransaction(productId, TransactionType.OUT, quantity, reason, userId);
    }
    
    /**
     * Create a new inventory transaction
     */
    public InventoryTransaction createTransaction(Long productId, TransactionType transactionType, 
                                               Integer quantity, String reason, Long userId) 
            throws DatabaseException, ValidationException {
        logger.info("Creating transaction: {} {} units of product {} by user {} - {}", 
            transactionType, quantity, productId, userId, reason);
        
        // Validate input
        validateTransactionInput(productId, transactionType, quantity, reason, userId);
        
        // Verify product exists
        Optional<Product> product = productDao.findById(productId);
        if (product.isEmpty()) {
            throw new ValidationException("Product not found: " + productId);
        }
        
        // Verify user exists
        Optional<User> user = userDao.findById(userId);
        if (user.isEmpty()) {
            throw new ValidationException("User not found: " + userId);
        }
        
        InventoryTransaction transaction = new InventoryTransaction(
            productId, transactionType, quantity, reason, userId
        );
        
        InventoryTransaction saved = transactionDao.save(transaction);
        logger.info("Successfully created transaction: {}", saved.getTransactionId());
        
        return saved;
    }
    
    /**
     * Update existing transaction
     */
    public InventoryTransaction updateTransaction(InventoryTransaction transaction) 
            throws DatabaseException, ValidationException {
        logger.info("Updating transaction: {}", transaction.getTransactionId());
        
        // Validate input
        validateTransactionInput(transaction.getProductId(), transaction.getTransactionType(),
            transaction.getQuantity(), transaction.getReason(), transaction.getUserId());
        
        // Check if transaction exists
        Optional<InventoryTransaction> existing = transactionDao.findById(transaction.getTransactionId());
        if (existing.isEmpty()) {
            throw new ValidationException("Transaction not found: " + transaction.getTransactionId());
        }
        
        InventoryTransaction updated = transactionDao.save(transaction);
        logger.info("Successfully updated transaction: {}", transaction.getTransactionId());
        return updated;
    }
    
    /**
     * Delete transaction
     */
    public boolean deleteTransaction(Long transactionId) throws DatabaseException, ValidationException {
        logger.info("Deleting transaction: {}", transactionId);
        
        // Check if transaction exists
        Optional<InventoryTransaction> existing = transactionDao.findById(transactionId);
        if (existing.isEmpty()) {
            throw new ValidationException("Transaction not found: " + transactionId);
        }
        
        boolean deleted = transactionDao.deleteById(transactionId);
        if (deleted) {
            logger.info("Successfully deleted transaction: {}", transactionId);
        }
        return deleted;
    }
    
    /**
     * Get transaction statistics for a product
     */
    public TransactionSummary getProductTransactionSummary(Long productId) throws DatabaseException {
        List<InventoryTransaction> transactions = transactionDao.findByProductId(productId);
        
        int totalIn = 0;
        int totalOut = 0;
        int transactionCount = transactions.size();
        
        for (InventoryTransaction transaction : transactions) {
            if (transaction.getTransactionType() == TransactionType.IN) {
                totalIn += transaction.getQuantity();
            } else {
                totalOut += transaction.getQuantity();
            }
        }
        
        return new TransactionSummary(productId, totalIn, totalOut, transactionCount);
    }
    
    /**
     * Validate transaction input data
     */
    private void validateTransactionInput(Long productId, TransactionType transactionType, 
                                        Integer quantity, String reason, Long userId) 
            throws ValidationException {
        if (productId == null || productId <= 0) {
            throw new ValidationException("Valid product ID is required");
        }
        
        if (transactionType == null) {
            throw new ValidationException("Transaction type is required");
        }
        
        if (quantity == null || quantity <= 0) {
            throw new ValidationException("Quantity must be a positive number");
        }
        
        if (quantity > 10000) {
            throw new ValidationException("Quantity cannot exceed 10,000 units per transaction");
        }
        
        if (reason == null || reason.trim().isEmpty()) {
            throw new ValidationException("Reason is required");
        }
        
        if (reason.length() > 255) {
            throw new ValidationException("Reason cannot be longer than 255 characters");
        }
        
        if (userId == null || userId <= 0) {
            throw new ValidationException("Valid user ID is required");
        }
    }
    
    /**
     * Inner class for transaction summary
     */
    public static class TransactionSummary {
        private final Long productId;
        private final int totalStockIn;
        private final int totalStockOut;
        private final int transactionCount;
        
        public TransactionSummary(Long productId, int totalStockIn, int totalStockOut, int transactionCount) {
            this.productId = productId;
            this.totalStockIn = totalStockIn;
            this.totalStockOut = totalStockOut;
            this.transactionCount = transactionCount;
        }
        
        public Long getProductId() { return productId; }
        public int getTotalStockIn() { return totalStockIn; }
        public int getTotalStockOut() { return totalStockOut; }
        public int getNetStock() { return totalStockIn - totalStockOut; }
        public int getTransactionCount() { return transactionCount; }
        
        @Override
        public String toString() {
            return String.format("TransactionSummary{productId=%d, stockIn=%d, stockOut=%d, net=%d, transactions=%d}", 
                productId, totalStockIn, totalStockOut, getNetStock(), transactionCount);
        }
    }
}