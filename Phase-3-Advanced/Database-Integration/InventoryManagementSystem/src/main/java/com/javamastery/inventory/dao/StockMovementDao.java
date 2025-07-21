package com.javamastery.inventory.dao;

import com.javamastery.inventory.model.MovementType;
import com.javamastery.inventory.model.ReferenceType;
import com.javamastery.inventory.model.StockMovement;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Data Access Object for StockMovement entities
 */
public class StockMovementDao extends BaseDao<StockMovement, Long> {
    
    public StockMovementDao() {
        super();
    }
    
    public StockMovementDao(DataSource dataSource) {
        super(dataSource);
    }
    
    @Override
    protected String getTableName() {
        return "stock_movements";
    }
    
    @Override
    protected String getIdColumnName() {
        return "movement_id";
    }
    
    @Override
    protected String getInsertSql() {
        return "INSERT INTO stock_movements (product_id, movement_type, quantity, reference_type, reference_id, notes, created_by) VALUES (?, ?, ?, ?, ?, ?, ?)";
    }
    
    @Override
    protected String getUpdateSql() {
        return "UPDATE stock_movements SET product_id = ?, movement_type = ?, quantity = ?, reference_type = ?, reference_id = ?, notes = ?, created_by = ? WHERE movement_id = ?";
    }
    
    @Override
    protected StockMovement mapResultSetToEntity(ResultSet rs) throws SQLException {
        StockMovement movement = new StockMovement();
        movement.setMovementId(rs.getLong("movement_id"));
        movement.setProductId(rs.getLong("product_id"));
        movement.setMovementType(MovementType.valueOf(rs.getString("movement_type")));
        movement.setQuantity(rs.getInt("quantity"));
        movement.setReferenceType(ReferenceType.valueOf(rs.getString("reference_type")));
        
        Long referenceId = rs.getLong("reference_id");
        if (!rs.wasNull()) {
            movement.setReferenceId(referenceId);
        }
        
        movement.setNotes(rs.getString("notes"));
        movement.setCreatedBy(rs.getString("created_by"));
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            movement.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        // Try to get product information if available (from join query)
        try {
            String productName = rs.getString("product_name");
            if (productName != null) {
                movement.setProductName(productName);
            }
            
            String productSku = rs.getString("product_sku");
            if (productSku != null) {
                movement.setProductSku(productSku);
            }
        } catch (SQLException e) {
            // Product columns not available, ignore
        }
        
        return movement;
    }
    
    @Override
    protected void setInsertParameters(PreparedStatement stmt, StockMovement movement) throws SQLException {
        stmt.setLong(1, movement.getProductId());
        stmt.setString(2, movement.getMovementType().name());
        stmt.setInt(3, movement.getQuantity());
        stmt.setString(4, movement.getReferenceType().name());
        
        if (movement.getReferenceId() != null) {
            stmt.setLong(5, movement.getReferenceId());
        } else {
            stmt.setNull(5, Types.BIGINT);
        }
        
        stmt.setString(6, movement.getNotes());
        stmt.setString(7, movement.getCreatedBy());
    }
    
    @Override
    protected void setUpdateParameters(PreparedStatement stmt, StockMovement movement) throws SQLException {
        stmt.setLong(1, movement.getProductId());
        stmt.setString(2, movement.getMovementType().name());
        stmt.setInt(3, movement.getQuantity());
        stmt.setString(4, movement.getReferenceType().name());
        
        if (movement.getReferenceId() != null) {
            stmt.setLong(5, movement.getReferenceId());
        } else {
            stmt.setNull(5, Types.BIGINT);
        }
        
        stmt.setString(6, movement.getNotes());
        stmt.setString(7, movement.getCreatedBy());
        stmt.setLong(8, movement.getMovementId());
    }
    
    @Override
    protected boolean hasId(StockMovement movement) {
        return movement.getMovementId() != null;
    }
    
    @Override
    protected Long getId(StockMovement movement) {
        return movement.getMovementId();
    }
    
    @Override
    protected void setGeneratedId(StockMovement movement, Long id) {
        movement.setMovementId(id);
    }
    
    // Additional stock movement-specific methods
    
    /**
     * Find all stock movements with product information
     */
    public List<StockMovement> findAllWithProductInfo() {
        String sql = """
            SELECT sm.*, p.name as product_name, p.sku as product_sku
            FROM stock_movements sm 
            JOIN products p ON sm.product_id = p.product_id 
            ORDER BY sm.created_at DESC
            """;
        return executeQuery(sql);
    }
    
    /**
     * Find stock movements by product ID
     */
    public List<StockMovement> findByProductId(Long productId) {
        String sql = """
            SELECT sm.*, p.name as product_name, p.sku as product_sku
            FROM stock_movements sm 
            JOIN products p ON sm.product_id = p.product_id 
            WHERE sm.product_id = ? 
            ORDER BY sm.created_at DESC
            """;
        return executeQuery(sql, productId);
    }
    
    /**
     * Find stock movements by movement type
     */
    public List<StockMovement> findByMovementType(MovementType movementType) {
        String sql = """
            SELECT sm.*, p.name as product_name, p.sku as product_sku
            FROM stock_movements sm 
            JOIN products p ON sm.product_id = p.product_id 
            WHERE sm.movement_type = ? 
            ORDER BY sm.created_at DESC
            """;
        return executeQuery(sql, movementType.name());
    }
    
    /**
     * Find stock movements by reference
     */
    public List<StockMovement> findByReference(ReferenceType referenceType, Long referenceId) {
        String sql = """
            SELECT sm.*, p.name as product_name, p.sku as product_sku
            FROM stock_movements sm 
            JOIN products p ON sm.product_id = p.product_id 
            WHERE sm.reference_type = ? AND sm.reference_id = ?
            ORDER BY sm.created_at DESC
            """;
        return executeQuery(sql, referenceType.name(), referenceId);
    }
    
    /**
     * Find stock movements by date range
     */
    public List<StockMovement> findByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        String sql = """
            SELECT sm.*, p.name as product_name, p.sku as product_sku
            FROM stock_movements sm 
            JOIN products p ON sm.product_id = p.product_id 
            WHERE sm.created_at >= ? AND sm.created_at <= ?
            ORDER BY sm.created_at DESC
            """;
        return executeQuery(sql, Timestamp.valueOf(startDate), Timestamp.valueOf(endDate));
    }
    
    /**
     * Find stock movements by user
     */
    public List<StockMovement> findByUser(String user) {
        String sql = """
            SELECT sm.*, p.name as product_name, p.sku as product_sku
            FROM stock_movements sm 
            JOIN products p ON sm.product_id = p.product_id 
            WHERE sm.created_by = ?
            ORDER BY sm.created_at DESC
            """;
        return executeQuery(sql, user);
    }
    
    /**
     * Find recent stock movements (last N records)
     */
    public List<StockMovement> findRecentMovements(int limit) {
        String sql = """
            SELECT sm.*, p.name as product_name, p.sku as product_sku
            FROM stock_movements sm 
            JOIN products p ON sm.product_id = p.product_id 
            ORDER BY sm.created_at DESC 
            LIMIT ?
            """;
        return executeQuery(sql, limit);
    }
    
    /**
     * Get movement statistics for a product
     */
    public MovementStatistics getMovementStatistics(Long productId) {
        String sql = """
            SELECT 
                SUM(CASE WHEN movement_type = 'IN' THEN quantity ELSE 0 END) as total_in,
                SUM(CASE WHEN movement_type = 'OUT' THEN ABS(quantity) ELSE 0 END) as total_out,
                SUM(CASE WHEN movement_type = 'ADJUSTMENT' AND quantity > 0 THEN quantity ELSE 0 END) as positive_adjustments,
                SUM(CASE WHEN movement_type = 'ADJUSTMENT' AND quantity < 0 THEN ABS(quantity) ELSE 0 END) as negative_adjustments,
                COUNT(*) as total_movements
            FROM stock_movements 
            WHERE product_id = ?
            """;
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, productId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new MovementStatistics(
                        rs.getInt("total_in"),
                        rs.getInt("total_out"),
                        rs.getInt("positive_adjustments"),
                        rs.getInt("negative_adjustments"),
                        rs.getInt("total_movements")
                    );
                }
            }
            
        } catch (SQLException e) {
            logger.error("Error getting movement statistics for product: {}", productId, e);
            throw new RuntimeException("Failed to get movement statistics", e);
        }
        
        return new MovementStatistics(0, 0, 0, 0, 0);
    }
    
    /**
     * Inner class for movement statistics
     */
    public static class MovementStatistics {
        private final int totalIn;
        private final int totalOut;
        private final int positiveAdjustments;
        private final int negativeAdjustments;
        private final int totalMovements;
        
        public MovementStatistics(int totalIn, int totalOut, int positiveAdjustments, 
                                 int negativeAdjustments, int totalMovements) {
            this.totalIn = totalIn;
            this.totalOut = totalOut;
            this.positiveAdjustments = positiveAdjustments;
            this.negativeAdjustments = negativeAdjustments;
            this.totalMovements = totalMovements;
        }
        
        public int getTotalIn() { return totalIn; }
        public int getTotalOut() { return totalOut; }
        public int getPositiveAdjustments() { return positiveAdjustments; }
        public int getNegativeAdjustments() { return negativeAdjustments; }
        public int getTotalMovements() { return totalMovements; }
        public int getNetMovement() { return totalIn - totalOut + positiveAdjustments - negativeAdjustments; }
        
        @Override
        public String toString() {
            return "MovementStatistics{" +
                   "totalIn=" + totalIn +
                   ", totalOut=" + totalOut +
                   ", positiveAdjustments=" + positiveAdjustments +
                   ", negativeAdjustments=" + negativeAdjustments +
                   ", totalMovements=" + totalMovements +
                   ", netMovement=" + getNetMovement() +
                   '}';
        }
    }
}