package com.javamastery.inventory.dao;

import com.javamastery.inventory.model.Product;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object for Product entities
 */
public class ProductDao extends BaseDao<Product, Long> {
    
    public ProductDao() {
        super();
    }
    
    public ProductDao(DataSource dataSource) {
        super(dataSource);
    }
    
    @Override
    protected String getTableName() {
        return "products";
    }
    
    @Override
    protected String getIdColumnName() {
        return "product_id";
    }
    
    @Override
    protected String getInsertSql() {
        return "INSERT INTO products (name, description, sku, category_id, unit_price, cost_price, reorder_level) VALUES (?, ?, ?, ?, ?, ?, ?)";
    }
    
    @Override
    protected String getUpdateSql() {
        return "UPDATE products SET name = ?, description = ?, sku = ?, category_id = ?, unit_price = ?, cost_price = ?, reorder_level = ? WHERE product_id = ?";
    }
    
    @Override
    protected Product mapResultSetToEntity(ResultSet rs) throws SQLException {
        Product product = new Product();
        product.setProductId(rs.getLong("product_id"));
        product.setName(rs.getString("name"));
        product.setDescription(rs.getString("description"));
        product.setSku(rs.getString("sku"));
        
        Long categoryId = rs.getLong("category_id");
        if (!rs.wasNull()) {
            product.setCategoryId(categoryId);
        }
        
        product.setUnitPrice(rs.getBigDecimal("unit_price"));
        product.setCostPrice(rs.getBigDecimal("cost_price"));
        product.setReorderLevel(rs.getInt("reorder_level"));
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            product.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            product.setUpdatedAt(updatedAt.toLocalDateTime());
        }
        
        // Try to get category name if available (from join query)
        try {
            String categoryName = rs.getString("category_name");
            if (categoryName != null) {
                product.setCategoryName(categoryName);
            }
        } catch (SQLException e) {
            // Category name column not available, ignore
        }
        
        return product;
    }
    
    @Override
    protected void setInsertParameters(PreparedStatement stmt, Product product) throws SQLException {
        stmt.setString(1, product.getName());
        stmt.setString(2, product.getDescription());
        stmt.setString(3, product.getSku());
        
        if (product.getCategoryId() != null) {
            stmt.setLong(4, product.getCategoryId());
        } else {
            stmt.setNull(4, Types.BIGINT);
        }
        
        stmt.setBigDecimal(5, product.getUnitPrice());
        stmt.setBigDecimal(6, product.getCostPrice());
        stmt.setInt(7, product.getReorderLevel());
    }
    
    @Override
    protected void setUpdateParameters(PreparedStatement stmt, Product product) throws SQLException {
        stmt.setString(1, product.getName());
        stmt.setString(2, product.getDescription());
        stmt.setString(3, product.getSku());
        
        if (product.getCategoryId() != null) {
            stmt.setLong(4, product.getCategoryId());
        } else {
            stmt.setNull(4, Types.BIGINT);
        }
        
        stmt.setBigDecimal(5, product.getUnitPrice());
        stmt.setBigDecimal(6, product.getCostPrice());
        stmt.setInt(7, product.getReorderLevel());
        stmt.setLong(8, product.getProductId());
    }
    
    @Override
    protected boolean hasId(Product product) {
        return product.getProductId() != null;
    }
    
    @Override
    protected Long getId(Product product) {
        return product.getProductId();
    }
    
    @Override
    protected void setGeneratedId(Product product, Long id) {
        product.setProductId(id);
    }
    
    // Additional product-specific methods
    
    /**
     * Find all products with category information
     */
    public List<Product> findAllWithCategory() {
        String sql = """
            SELECT p.*, c.name as category_name 
            FROM products p 
            LEFT JOIN categories c ON p.category_id = c.category_id 
            ORDER BY p.name
            """;
        return executeQuery(sql);
    }
    
    /**
     * Find product by SKU
     */
    public Optional<Product> findBySku(String sku) {
        String sql = """
            SELECT p.*, c.name as category_name 
            FROM products p 
            LEFT JOIN categories c ON p.category_id = c.category_id 
            WHERE p.sku = ?
            """;
        return executeQueryForSingleResult(sql, sku);
    }
    
    /**
     * Find products by category
     */
    public List<Product> findByCategory(Long categoryId) {
        String sql = """
            SELECT p.*, c.name as category_name 
            FROM products p 
            LEFT JOIN categories c ON p.category_id = c.category_id 
            WHERE p.category_id = ? 
            ORDER BY p.name
            """;
        return executeQuery(sql, categoryId);
    }
    
    /**
     * Find products by name pattern (case-insensitive)
     */
    public List<Product> findByNameContaining(String namePattern) {
        String sql = """
            SELECT p.*, c.name as category_name 
            FROM products p 
            LEFT JOIN categories c ON p.category_id = c.category_id 
            WHERE LOWER(p.name) LIKE LOWER(?) 
            ORDER BY p.name
            """;
        return executeQuery(sql, "%" + namePattern + "%");
    }
    
    /**
     * Search products by multiple criteria
     */
    public List<Product> searchProducts(String searchTerm) {
        String sql = """
            SELECT p.*, c.name as category_name 
            FROM products p 
            LEFT JOIN categories c ON p.category_id = c.category_id 
            WHERE LOWER(p.name) LIKE LOWER(?) 
               OR LOWER(p.description) LIKE LOWER(?) 
               OR LOWER(p.sku) LIKE LOWER(?) 
               OR LOWER(c.name) LIKE LOWER(?)
            ORDER BY p.name
            """;
        String pattern = "%" + searchTerm + "%";
        return executeQuery(sql, pattern, pattern, pattern, pattern);
    }
    
    /**
     * Find products by price range
     */
    public List<Product> findByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        String sql = """
            SELECT p.*, c.name as category_name 
            FROM products p 
            LEFT JOIN categories c ON p.category_id = c.category_id 
            WHERE p.unit_price >= ? AND p.unit_price <= ? 
            ORDER BY p.unit_price
            """;
        return executeQuery(sql, minPrice, maxPrice);
    }
    
    /**
     * Find products below reorder level
     */
    public List<Product> findLowStockProducts() {
        String sql = """
            SELECT p.*, c.name as category_name, i.quantity_on_hand, i.quantity_allocated
            FROM products p 
            LEFT JOIN categories c ON p.category_id = c.category_id 
            LEFT JOIN inventory i ON p.product_id = i.product_id
            WHERE (i.quantity_on_hand - i.quantity_allocated) <= p.reorder_level
            ORDER BY (i.quantity_on_hand - i.quantity_allocated), p.name
            """;
        return executeQuery(sql);
    }
    
    /**
     * Check if SKU exists (for uniqueness validation)
     */
    public boolean existsBySku(String sku) {
        String sql = "SELECT 1 FROM products WHERE sku = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, sku);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
            
        } catch (SQLException e) {
            logger.error("Error checking if SKU exists: {}", sku, e);
            throw new RuntimeException("Failed to check SKU existence", e);
        }
    }
    
    /**
     * Check if SKU exists for a different product (for update validation)
     */
    public boolean existsBySkuAndNotId(String sku, Long productId) {
        String sql = "SELECT 1 FROM products WHERE sku = ? AND product_id != ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, sku);
            stmt.setLong(2, productId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
            
        } catch (SQLException e) {
            logger.error("Error checking if SKU exists for different id: {} {}", sku, productId, e);
            throw new RuntimeException("Failed to check SKU existence", e);
        }
    }
    
    /**
     * Get product count by category
     */
    public long countByCategory(Long categoryId) {
        String sql = "SELECT COUNT(*) FROM products WHERE category_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, categoryId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
                return 0;
            }
            
        } catch (SQLException e) {
            logger.error("Error counting products by category: {}", categoryId, e);
            throw new RuntimeException("Failed to count products by category", e);
        }
    }
}