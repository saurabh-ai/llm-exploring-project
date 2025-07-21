package com.javamastery.inventory.dao;

import com.javamastery.inventory.model.Inventory;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object for Inventory entities
 */
public class InventoryDao extends BaseDao<Inventory, Long> {
    
    public InventoryDao() {
        super();
    }
    
    public InventoryDao(DataSource dataSource) {
        super(dataSource);
    }
    
    @Override
    protected String getTableName() {
        return "inventory";
    }
    
    @Override
    protected String getIdColumnName() {
        return "inventory_id";
    }
    
    @Override
    protected String getInsertSql() {
        return "INSERT INTO inventory (product_id, quantity_on_hand, quantity_allocated, location) VALUES (?, ?, ?, ?)";
    }
    
    @Override
    protected String getUpdateSql() {
        return "UPDATE inventory SET product_id = ?, quantity_on_hand = ?, quantity_allocated = ?, location = ? WHERE inventory_id = ?";
    }
    
    @Override
    protected Inventory mapResultSetToEntity(ResultSet rs) throws SQLException {
        Inventory inventory = new Inventory();
        inventory.setInventoryId(rs.getLong("inventory_id"));
        inventory.setProductId(rs.getLong("product_id"));
        inventory.setQuantityOnHand(rs.getInt("quantity_on_hand"));
        inventory.setQuantityAllocated(rs.getInt("quantity_allocated"));
        inventory.setLocation(rs.getString("location"));
        
        Timestamp lastUpdated = rs.getTimestamp("last_updated");
        if (lastUpdated != null) {
            inventory.setLastUpdated(lastUpdated.toLocalDateTime());
        }
        
        // Try to get product information if available (from join query)
        try {
            String productName = rs.getString("product_name");
            if (productName != null) {
                inventory.setProductName(productName);
            }
            
            String productSku = rs.getString("product_sku");
            if (productSku != null) {
                inventory.setProductSku(productSku);
            }
            
            int reorderLevel = rs.getInt("reorder_level");
            if (!rs.wasNull()) {
                inventory.setReorderLevel(reorderLevel);
            }
        } catch (SQLException e) {
            // Product columns not available, ignore
        }
        
        return inventory;
    }
    
    @Override
    protected void setInsertParameters(PreparedStatement stmt, Inventory inventory) throws SQLException {
        stmt.setLong(1, inventory.getProductId());
        stmt.setInt(2, inventory.getQuantityOnHand());
        stmt.setInt(3, inventory.getQuantityAllocated());
        stmt.setString(4, inventory.getLocation());
    }
    
    @Override
    protected void setUpdateParameters(PreparedStatement stmt, Inventory inventory) throws SQLException {
        stmt.setLong(1, inventory.getProductId());
        stmt.setInt(2, inventory.getQuantityOnHand());
        stmt.setInt(3, inventory.getQuantityAllocated());
        stmt.setString(4, inventory.getLocation());
        stmt.setLong(5, inventory.getInventoryId());
    }
    
    @Override
    protected boolean hasId(Inventory inventory) {
        return inventory.getInventoryId() != null;
    }
    
    @Override
    protected Long getId(Inventory inventory) {
        return inventory.getInventoryId();
    }
    
    @Override
    protected void setGeneratedId(Inventory inventory, Long id) {
        inventory.setInventoryId(id);
    }
    
    // Additional inventory-specific methods
    
    /**
     * Find all inventory with product information
     */
    public List<Inventory> findAllWithProductInfo() {
        String sql = """
            SELECT i.*, p.name as product_name, p.sku as product_sku, p.reorder_level
            FROM inventory i 
            JOIN products p ON i.product_id = p.product_id 
            ORDER BY p.name, i.location
            """;
        return executeQuery(sql);
    }
    
    /**
     * Find inventory by product ID
     */
    public Optional<Inventory> findByProductId(Long productId) {
        String sql = """
            SELECT i.*, p.name as product_name, p.sku as product_sku, p.reorder_level
            FROM inventory i 
            JOIN products p ON i.product_id = p.product_id 
            WHERE i.product_id = ?
            """;
        return executeQueryForSingleResult(sql, productId);
    }
    
    /**
     * Find inventory by product ID and location
     */
    public Optional<Inventory> findByProductIdAndLocation(Long productId, String location) {
        String sql = """
            SELECT i.*, p.name as product_name, p.sku as product_sku, p.reorder_level
            FROM inventory i 
            JOIN products p ON i.product_id = p.product_id 
            WHERE i.product_id = ? AND i.location = ?
            """;
        return executeQueryForSingleResult(sql, productId, location);
    }
    
    /**
     * Find all inventory by location
     */
    public List<Inventory> findByLocation(String location) {
        String sql = """
            SELECT i.*, p.name as product_name, p.sku as product_sku, p.reorder_level
            FROM inventory i 
            JOIN products p ON i.product_id = p.product_id 
            WHERE i.location = ? 
            ORDER BY p.name
            """;
        return executeQuery(sql, location);
    }
    
    /**
     * Find low stock items (available quantity <= reorder level)
     */
    public List<Inventory> findLowStockItems() {
        String sql = """
            SELECT i.*, p.name as product_name, p.sku as product_sku, p.reorder_level
            FROM inventory i 
            JOIN products p ON i.product_id = p.product_id 
            WHERE (i.quantity_on_hand - i.quantity_allocated) <= p.reorder_level
            ORDER BY (i.quantity_on_hand - i.quantity_allocated), p.name
            """;
        return executeQuery(sql);
    }
    
    /**
     * Find out of stock items (available quantity = 0)
     */
    public List<Inventory> findOutOfStockItems() {
        String sql = """
            SELECT i.*, p.name as product_name, p.sku as product_sku, p.reorder_level
            FROM inventory i 
            JOIN products p ON i.product_id = p.product_id 
            WHERE (i.quantity_on_hand - i.quantity_allocated) = 0
            ORDER BY p.name
            """;
        return executeQuery(sql);
    }
    
    /**
     * Update stock quantities for a product
     */
    public boolean updateStock(Long productId, String location, int quantityOnHand, int quantityAllocated) {
        String sql = "UPDATE inventory SET quantity_on_hand = ?, quantity_allocated = ? WHERE product_id = ? AND location = ?";
        int rowsAffected = executeUpdate(sql, quantityOnHand, quantityAllocated, productId, location);
        return rowsAffected > 0;
    }
    
    /**
     * Adjust stock quantity (add or subtract)
     */
    public boolean adjustStock(Long productId, String location, int quantityChange) {
        String sql = "UPDATE inventory SET quantity_on_hand = quantity_on_hand + ? WHERE product_id = ? AND location = ?";
        int rowsAffected = executeUpdate(sql, quantityChange, productId, location);
        return rowsAffected > 0;
    }
    
    /**
     * Allocate stock for an order
     */
    public boolean allocateStock(Long productId, String location, int quantity) {
        // First check if enough stock is available
        String checkSql = "SELECT (quantity_on_hand - quantity_allocated) as available FROM inventory WHERE product_id = ? AND location = ?";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
            
            checkStmt.setLong(1, productId);
            checkStmt.setString(2, location);
            
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next()) {
                    int available = rs.getInt("available");
                    if (available >= quantity) {
                        // Proceed with allocation
                        String updateSql = "UPDATE inventory SET quantity_allocated = quantity_allocated + ? WHERE product_id = ? AND location = ?";
                        int rowsAffected = executeUpdate(updateSql, quantity, productId, location);
                        return rowsAffected > 0;
                    }
                }
            }
            
        } catch (SQLException e) {
            logger.error("Error allocating stock for product {} at location {}: quantity {}", productId, location, quantity, e);
            throw new RuntimeException("Failed to allocate stock", e);
        }
        
        return false; // Not enough stock available
    }
    
    /**
     * Deallocate stock (release allocation)
     */
    public boolean deallocateStock(Long productId, String location, int quantity) {
        String sql = "UPDATE inventory SET quantity_allocated = quantity_allocated - ? WHERE product_id = ? AND location = ? AND quantity_allocated >= ?";
        int rowsAffected = executeUpdate(sql, quantity, productId, location, quantity);
        return rowsAffected > 0;
    }
    
    /**
     * Get total quantity on hand across all locations for a product
     */
    public int getTotalQuantityOnHand(Long productId) {
        String sql = "SELECT SUM(quantity_on_hand) FROM inventory WHERE product_id = ?";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, productId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
                return 0;
            }
            
        } catch (SQLException e) {
            logger.error("Error getting total quantity on hand for product: {}", productId, e);
            throw new RuntimeException("Failed to get total quantity on hand", e);
        }
    }
    
    /**
     * Get total available quantity across all locations for a product
     */
    public int getTotalAvailableQuantity(Long productId) {
        String sql = "SELECT SUM(quantity_on_hand - quantity_allocated) FROM inventory WHERE product_id = ?";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, productId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
                return 0;
            }
            
        } catch (SQLException e) {
            logger.error("Error getting total available quantity for product: {}", productId, e);
            throw new RuntimeException("Failed to get total available quantity", e);
        }
    }
    
    /**
     * Get all distinct locations
     */
    public List<String> findAllLocations() {
        String sql = "SELECT DISTINCT location FROM inventory ORDER BY location";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            List<String> locations = new java.util.ArrayList<>();
            while (rs.next()) {
                locations.add(rs.getString("location"));
            }
            return locations;
            
        } catch (SQLException e) {
            logger.error("Error getting all locations", e);
            throw new RuntimeException("Failed to get all locations", e);
        }
    }
    
    /**
     * Create or update inventory record
     */
    public void createOrUpdate(Long productId, String location, int quantityOnHand, int quantityAllocated) {
        Optional<Inventory> existing = findByProductIdAndLocation(productId, location);
        
        if (existing.isPresent()) {
            Inventory inventory = existing.get();
            inventory.setQuantityOnHand(quantityOnHand);
            inventory.setQuantityAllocated(quantityAllocated);
            update(inventory);
        } else {
            Inventory inventory = new Inventory(productId, quantityOnHand, quantityAllocated, location);
            insert(inventory);
        }
    }
}