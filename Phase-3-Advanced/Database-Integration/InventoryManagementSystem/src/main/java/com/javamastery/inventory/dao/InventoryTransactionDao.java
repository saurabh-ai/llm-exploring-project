package com.javamastery.inventory.dao;

import com.javamastery.inventory.exception.DatabaseException;
import com.javamastery.inventory.model.InventoryTransaction;
import com.javamastery.inventory.model.TransactionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object for InventoryTransaction entities
 */
public class InventoryTransactionDao {
    private static final Logger logger = LoggerFactory.getLogger(InventoryTransactionDao.class);
    protected final DataSource dataSource;
    
    public InventoryTransactionDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    
    /**
     * Find all inventory transactions
     */
    public List<InventoryTransaction> findAll() throws DatabaseException {
        String sql = """
            SELECT it.transaction_id, it.product_id, it.transaction_type, it.quantity,
                   it.reason, it.transaction_date, it.user_id,
                   p.name as product_name, u.username
            FROM inventory_transactions it
            JOIN products p ON it.product_id = p.product_id
            JOIN users u ON it.user_id = u.user_id
            ORDER BY it.transaction_date DESC
            """;
        
        List<InventoryTransaction> transactions = new ArrayList<>();
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                transactions.add(mapResultSetToInventoryTransaction(rs));
            }
            
            logger.debug("Found {} inventory transactions", transactions.size());
            return transactions;
            
        } catch (SQLException e) {
            logger.error("Error finding all inventory transactions", e);
            throw new DatabaseException("Failed to retrieve inventory transactions", e);
        }
    }
    
    /**
     * Find inventory transaction by ID
     */
    public Optional<InventoryTransaction> findById(Long transactionId) throws DatabaseException {
        String sql = """
            SELECT it.transaction_id, it.product_id, it.transaction_type, it.quantity,
                   it.reason, it.transaction_date, it.user_id,
                   p.name as product_name, u.username
            FROM inventory_transactions it
            JOIN products p ON it.product_id = p.product_id
            JOIN users u ON it.user_id = u.user_id
            WHERE it.transaction_id = ?
            """;
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, transactionId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToInventoryTransaction(rs));
                }
                return Optional.empty();
            }
            
        } catch (SQLException e) {
            logger.error("Error finding inventory transaction by ID: {}", transactionId, e);
            throw new DatabaseException("Failed to find inventory transaction by ID", e);
        }
    }
    
    /**
     * Find inventory transactions by product ID
     */
    public List<InventoryTransaction> findByProductId(Long productId) throws DatabaseException {
        String sql = """
            SELECT it.transaction_id, it.product_id, it.transaction_type, it.quantity,
                   it.reason, it.transaction_date, it.user_id,
                   p.name as product_name, u.username
            FROM inventory_transactions it
            JOIN products p ON it.product_id = p.product_id
            JOIN users u ON it.user_id = u.user_id
            WHERE it.product_id = ?
            ORDER BY it.transaction_date DESC
            """;
        
        List<InventoryTransaction> transactions = new ArrayList<>();
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, productId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    transactions.add(mapResultSetToInventoryTransaction(rs));
                }
            }
            
            logger.debug("Found {} inventory transactions for product ID: {}", transactions.size(), productId);
            return transactions;
            
        } catch (SQLException e) {
            logger.error("Error finding inventory transactions by product ID: {}", productId, e);
            throw new DatabaseException("Failed to find inventory transactions by product ID", e);
        }
    }
    
    /**
     * Find inventory transactions by user ID
     */
    public List<InventoryTransaction> findByUserId(Long userId) throws DatabaseException {
        String sql = """
            SELECT it.transaction_id, it.product_id, it.transaction_type, it.quantity,
                   it.reason, it.transaction_date, it.user_id,
                   p.name as product_name, u.username
            FROM inventory_transactions it
            JOIN products p ON it.product_id = p.product_id
            JOIN users u ON it.user_id = u.user_id
            WHERE it.user_id = ?
            ORDER BY it.transaction_date DESC
            """;
        
        List<InventoryTransaction> transactions = new ArrayList<>();
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, userId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    transactions.add(mapResultSetToInventoryTransaction(rs));
                }
            }
            
            logger.debug("Found {} inventory transactions for user ID: {}", transactions.size(), userId);
            return transactions;
            
        } catch (SQLException e) {
            logger.error("Error finding inventory transactions by user ID: {}", userId, e);
            throw new DatabaseException("Failed to find inventory transactions by user ID", e);
        }
    }
    
    /**
     * Find transactions within date range
     */
    public List<InventoryTransaction> findByDateRange(LocalDateTime startDate, LocalDateTime endDate) throws DatabaseException {
        String sql = """
            SELECT it.transaction_id, it.product_id, it.transaction_type, it.quantity,
                   it.reason, it.transaction_date, it.user_id,
                   p.name as product_name, u.username
            FROM inventory_transactions it
            JOIN products p ON it.product_id = p.product_id
            JOIN users u ON it.user_id = u.user_id
            WHERE it.transaction_date BETWEEN ? AND ?
            ORDER BY it.transaction_date DESC
            """;
        
        List<InventoryTransaction> transactions = new ArrayList<>();
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setTimestamp(1, Timestamp.valueOf(startDate));
            stmt.setTimestamp(2, Timestamp.valueOf(endDate));
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    transactions.add(mapResultSetToInventoryTransaction(rs));
                }
            }
            
            logger.debug("Found {} inventory transactions between {} and {}", transactions.size(), startDate, endDate);
            return transactions;
            
        } catch (SQLException e) {
            logger.error("Error finding inventory transactions by date range", e);
            throw new DatabaseException("Failed to find inventory transactions by date range", e);
        }
    }
    
    /**
     * Save (insert or update) inventory transaction
     */
    public InventoryTransaction save(InventoryTransaction transaction) throws DatabaseException {
        if (transaction.getTransactionId() == null) {
            return insert(transaction);
        } else {
            return update(transaction);
        }
    }
    
    /**
     * Insert new inventory transaction
     */
    private InventoryTransaction insert(InventoryTransaction transaction) throws DatabaseException {
        String sql = """
            INSERT INTO inventory_transactions (product_id, transaction_type, quantity, reason, user_id)
            VALUES (?, ?, ?, ?, ?)
            """;
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setLong(1, transaction.getProductId());
            stmt.setString(2, transaction.getTransactionType().name());
            stmt.setInt(3, transaction.getQuantity());
            stmt.setString(4, transaction.getReason());
            stmt.setLong(5, transaction.getUserId());
            
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new DatabaseException("Failed to insert inventory transaction, no rows affected");
            }
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    Long transactionId = generatedKeys.getLong(1);
                    transaction.setTransactionId(transactionId);
                    logger.info("Successfully inserted inventory transaction with id: {}", transactionId);
                    return findById(transactionId).orElse(transaction);
                } else {
                    throw new DatabaseException("Failed to get generated key for inventory transaction");
                }
            }
            
        } catch (SQLException e) {
            logger.error("Error inserting inventory transaction", e);
            throw new DatabaseException("Failed to insert inventory transaction", e);
        }
    }
    
    /**
     * Update existing inventory transaction
     */
    private InventoryTransaction update(InventoryTransaction transaction) throws DatabaseException {
        String sql = """
            UPDATE inventory_transactions
            SET product_id = ?, transaction_type = ?, quantity = ?, reason = ?, user_id = ?
            WHERE transaction_id = ?
            """;
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, transaction.getProductId());
            stmt.setString(2, transaction.getTransactionType().name());
            stmt.setInt(3, transaction.getQuantity());
            stmt.setString(4, transaction.getReason());
            stmt.setLong(5, transaction.getUserId());
            stmt.setLong(6, transaction.getTransactionId());
            
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new DatabaseException("Failed to update inventory transaction, transaction not found: " + transaction.getTransactionId());
            }
            
            logger.info("Successfully updated inventory transaction with id: {}", transaction.getTransactionId());
            return findById(transaction.getTransactionId()).orElse(transaction);
            
        } catch (SQLException e) {
            logger.error("Error updating inventory transaction: {}", transaction.getTransactionId(), e);
            throw new DatabaseException("Failed to update inventory transaction", e);
        }
    }
    
    /**
     * Delete inventory transaction by ID
     */
    public boolean deleteById(Long transactionId) throws DatabaseException {
        String sql = "DELETE FROM inventory_transactions WHERE transaction_id = ?";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, transactionId);
            
            int rowsAffected = stmt.executeUpdate();
            boolean deleted = rowsAffected > 0;
            
            if (deleted) {
                logger.info("Successfully deleted inventory transaction with id: {}", transactionId);
            } else {
                logger.warn("No inventory transaction found with id: {}", transactionId);
            }
            
            return deleted;
            
        } catch (SQLException e) {
            logger.error("Error deleting inventory transaction: {}", transactionId, e);
            throw new DatabaseException("Failed to delete inventory transaction", e);
        }
    }
    
    /**
     * Map ResultSet to InventoryTransaction object
     */
    private InventoryTransaction mapResultSetToInventoryTransaction(ResultSet rs) throws SQLException {
        Long transactionId = rs.getLong("transaction_id");
        Long productId = rs.getLong("product_id");
        TransactionType transactionType = TransactionType.valueOf(rs.getString("transaction_type"));
        Integer quantity = rs.getInt("quantity");
        String reason = rs.getString("reason");
        
        Timestamp transactionTimestamp = rs.getTimestamp("transaction_date");
        LocalDateTime transactionDate = transactionTimestamp != null ? transactionTimestamp.toLocalDateTime() : null;
        
        Long userId = rs.getLong("user_id");
        
        InventoryTransaction transaction = new InventoryTransaction(
            transactionId, productId, transactionType, quantity, reason, transactionDate, userId
        );
        
        // Set optional joined fields
        transaction.setProductName(rs.getString("product_name"));
        transaction.setUsername(rs.getString("username"));
        
        return transaction;
    }
}