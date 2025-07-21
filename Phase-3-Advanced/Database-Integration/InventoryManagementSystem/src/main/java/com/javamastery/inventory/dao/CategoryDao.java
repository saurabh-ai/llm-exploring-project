package com.javamastery.inventory.dao;

import com.javamastery.inventory.model.Category;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object for Category entities
 */
public class CategoryDao extends BaseDao<Category, Long> {
    
    public CategoryDao() {
        super();
    }
    
    public CategoryDao(DataSource dataSource) {
        super(dataSource);
    }
    
    @Override
    protected String getTableName() {
        return "categories";
    }
    
    @Override
    protected String getIdColumnName() {
        return "category_id";
    }
    
    @Override
    protected String getInsertSql() {
        return "INSERT INTO categories (name, description) VALUES (?, ?)";
    }
    
    @Override
    protected String getUpdateSql() {
        return "UPDATE categories SET name = ?, description = ? WHERE category_id = ?";
    }
    
    @Override
    protected Category mapResultSetToEntity(ResultSet rs) throws SQLException {
        Category category = new Category();
        category.setCategoryId(rs.getLong("category_id"));
        category.setName(rs.getString("name"));
        category.setDescription(rs.getString("description"));
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            category.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        return category;
    }
    
    @Override
    protected void setInsertParameters(PreparedStatement stmt, Category category) throws SQLException {
        stmt.setString(1, category.getName());
        stmt.setString(2, category.getDescription());
    }
    
    @Override
    protected void setUpdateParameters(PreparedStatement stmt, Category category) throws SQLException {
        stmt.setString(1, category.getName());
        stmt.setString(2, category.getDescription());
        stmt.setLong(3, category.getCategoryId());
    }
    
    @Override
    protected boolean hasId(Category category) {
        return category.getCategoryId() != null;
    }
    
    @Override
    protected Long getId(Category category) {
        return category.getCategoryId();
    }
    
    @Override
    protected void setGeneratedId(Category category, Long id) {
        category.setCategoryId(id);
    }
    
    // Additional category-specific methods
    
    /**
     * Find category by name
     */
    public Optional<Category> findByName(String name) {
        String sql = "SELECT * FROM categories WHERE name = ?";
        return executeQueryForSingleResult(sql, name);
    }
    
    /**
     * Find categories by name pattern (case-insensitive)
     */
    public List<Category> findByNameContaining(String namePattern) {
        String sql = "SELECT * FROM categories WHERE LOWER(name) LIKE LOWER(?) ORDER BY name";
        return executeQuery(sql, "%" + namePattern + "%");
    }
    
    /**
     * Check if category name exists (for uniqueness validation)
     */
    public boolean existsByName(String name) {
        String sql = "SELECT 1 FROM categories WHERE name = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, name);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
            
        } catch (SQLException e) {
            logger.error("Error checking if category name exists: {}", name, e);
            throw new RuntimeException("Failed to check category name existence", e);
        }
    }
    
    /**
     * Check if category name exists for a different category (for update validation)
     */
    public boolean existsByNameAndNotId(String name, Long categoryId) {
        String sql = "SELECT 1 FROM categories WHERE name = ? AND category_id != ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, name);
            stmt.setLong(2, categoryId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
            
        } catch (SQLException e) {
            logger.error("Error checking if category name exists for different id: {} {}", name, categoryId, e);
            throw new RuntimeException("Failed to check category name existence", e);
        }
    }
}