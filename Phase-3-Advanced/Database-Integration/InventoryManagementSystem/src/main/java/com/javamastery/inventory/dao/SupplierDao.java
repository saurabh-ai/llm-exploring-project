package com.javamastery.inventory.dao;

import com.javamastery.inventory.model.Supplier;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object for Supplier entities
 */
public class SupplierDao extends BaseDao<Supplier, Long> {
    
    public SupplierDao() {
        super();
    }
    
    public SupplierDao(DataSource dataSource) {
        super(dataSource);
    }
    
    @Override
    protected String getTableName() {
        return "suppliers";
    }
    
    @Override
    protected String getIdColumnName() {
        return "supplier_id";
    }
    
    @Override
    protected String getInsertSql() {
        return "INSERT INTO suppliers (name, contact_person, email, phone, address) VALUES (?, ?, ?, ?, ?)";
    }
    
    @Override
    protected String getUpdateSql() {
        return "UPDATE suppliers SET name = ?, contact_person = ?, email = ?, phone = ?, address = ? WHERE supplier_id = ?";
    }
    
    @Override
    protected Supplier mapResultSetToEntity(ResultSet rs) throws SQLException {
        Supplier supplier = new Supplier();
        supplier.setSupplierId(rs.getLong("supplier_id"));
        supplier.setName(rs.getString("name"));
        supplier.setContactPerson(rs.getString("contact_person"));
        supplier.setEmail(rs.getString("email"));
        supplier.setPhone(rs.getString("phone"));
        supplier.setAddress(rs.getString("address"));
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            supplier.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        return supplier;
    }
    
    @Override
    protected void setInsertParameters(PreparedStatement stmt, Supplier supplier) throws SQLException {
        stmt.setString(1, supplier.getName());
        stmt.setString(2, supplier.getContactPerson());
        stmt.setString(3, supplier.getEmail());
        stmt.setString(4, supplier.getPhone());
        stmt.setString(5, supplier.getAddress());
    }
    
    @Override
    protected void setUpdateParameters(PreparedStatement stmt, Supplier supplier) throws SQLException {
        stmt.setString(1, supplier.getName());
        stmt.setString(2, supplier.getContactPerson());
        stmt.setString(3, supplier.getEmail());
        stmt.setString(4, supplier.getPhone());
        stmt.setString(5, supplier.getAddress());
        stmt.setLong(6, supplier.getSupplierId());
    }
    
    @Override
    protected boolean hasId(Supplier supplier) {
        return supplier.getSupplierId() != null;
    }
    
    @Override
    protected Long getId(Supplier supplier) {
        return supplier.getSupplierId();
    }
    
    @Override
    protected void setGeneratedId(Supplier supplier, Long id) {
        supplier.setSupplierId(id);
    }
    
    // Additional supplier-specific methods
    
    /**
     * Find supplier by name
     */
    public Optional<Supplier> findByName(String name) {
        String sql = "SELECT * FROM suppliers WHERE name = ?";
        return executeQueryForSingleResult(sql, name);
    }
    
    /**
     * Find suppliers by name pattern (case-insensitive)
     */
    public List<Supplier> findByNameContaining(String namePattern) {
        String sql = "SELECT * FROM suppliers WHERE LOWER(name) LIKE LOWER(?) ORDER BY name";
        return executeQuery(sql, "%" + namePattern + "%");
    }
    
    /**
     * Find supplier by email
     */
    public Optional<Supplier> findByEmail(String email) {
        String sql = "SELECT * FROM suppliers WHERE email = ?";
        return executeQueryForSingleResult(sql, email);
    }
    
    /**
     * Find suppliers by contact person
     */
    public List<Supplier> findByContactPerson(String contactPerson) {
        String sql = "SELECT * FROM suppliers WHERE LOWER(contact_person) LIKE LOWER(?) ORDER BY name";
        return executeQuery(sql, "%" + contactPerson + "%");
    }
    
    /**
     * Search suppliers by multiple criteria
     */
    public List<Supplier> searchSuppliers(String searchTerm) {
        String sql = """
            SELECT * FROM suppliers 
            WHERE LOWER(name) LIKE LOWER(?) 
               OR LOWER(contact_person) LIKE LOWER(?) 
               OR LOWER(email) LIKE LOWER(?) 
               OR LOWER(phone) LIKE LOWER(?)
            ORDER BY name
            """;
        String pattern = "%" + searchTerm + "%";
        return executeQuery(sql, pattern, pattern, pattern, pattern);
    }
    
    /**
     * Check if supplier name exists (for uniqueness validation)
     */
    public boolean existsByName(String name) {
        String sql = "SELECT 1 FROM suppliers WHERE name = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, name);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
            
        } catch (SQLException e) {
            logger.error("Error checking if supplier name exists: {}", name, e);
            throw new RuntimeException("Failed to check supplier name existence", e);
        }
    }
    
    /**
     * Check if email exists (for uniqueness validation)
     */
    public boolean existsByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        
        String sql = "SELECT 1 FROM suppliers WHERE email = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
            
        } catch (SQLException e) {
            logger.error("Error checking if supplier email exists: {}", email, e);
            throw new RuntimeException("Failed to check supplier email existence", e);
        }
    }
    
    /**
     * Get all active suppliers (for dropdown lists)
     */
    public List<Supplier> findAllActive() {
        // For now, all suppliers are considered active
        // This method can be extended when a status field is added
        return findAll();
    }
}