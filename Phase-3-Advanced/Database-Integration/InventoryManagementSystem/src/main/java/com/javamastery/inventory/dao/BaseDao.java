package com.javamastery.inventory.dao;

import com.javamastery.inventory.config.DatabaseConfig;
import com.javamastery.inventory.exception.DatabaseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Base Data Access Object providing common CRUD operations and utility methods.
 * All DAOs should extend this class to inherit standard database operations.
 *
 * @param <T> The entity type
 * @param <ID> The ID type (typically Long)
 */
public abstract class BaseDao<T, ID> {
    protected final Logger logger = LoggerFactory.getLogger(getClass());
    protected final DataSource dataSource;
    
    public BaseDao() {
        this.dataSource = DatabaseConfig.getInstance().getDataSource();
    }
    
    public BaseDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    
    // Abstract methods that must be implemented by subclasses
    
    /**
     * Get the table name for this entity
     */
    protected abstract String getTableName();
    
    /**
     * Get the primary key column name
     */
    protected abstract String getIdColumnName();
    
    /**
     * Map a ResultSet row to an entity
     */
    protected abstract T mapResultSetToEntity(ResultSet rs) throws SQLException;
    
    /**
     * Set parameters for insert statement
     */
    protected abstract void setInsertParameters(PreparedStatement stmt, T entity) throws SQLException;
    
    /**
     * Set parameters for update statement
     */
    protected abstract void setUpdateParameters(PreparedStatement stmt, T entity) throws SQLException;
    
    /**
     * Get the insert SQL statement
     */
    protected abstract String getInsertSql();
    
    /**
     * Get the update SQL statement
     */
    protected abstract String getUpdateSql();
    
    // Common CRUD operations
    
    /**
     * Find an entity by its ID
     */
    public Optional<T> findById(ID id) {
        String sql = "SELECT * FROM " + getTableName() + " WHERE " + getIdColumnName() + " = ?";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setObject(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToEntity(rs));
                }
            }
            
        } catch (SQLException e) {
            logger.error("Error finding {} by id: {}", getTableName(), id, e);
            throw new DatabaseException("Failed to find " + getTableName() + " by id: " + id, e);
        }
        
        return Optional.empty();
    }
    
    /**
     * Find all entities
     */
    public List<T> findAll() {
        String sql = "SELECT * FROM " + getTableName() + " ORDER BY " + getIdColumnName();
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            List<T> entities = new ArrayList<>();
            while (rs.next()) {
                entities.add(mapResultSetToEntity(rs));
            }
            
            return entities;
            
        } catch (SQLException e) {
            logger.error("Error finding all {}", getTableName(), e);
            throw new DatabaseException("Failed to find all " + getTableName(), e);
        }
    }
    
    /**
     * Save an entity (insert or update based on ID presence)
     */
    public T save(T entity) {
        if (hasId(entity)) {
            return update(entity);
        } else {
            return insert(entity);
        }
    }
    
    /**
     * Insert a new entity
     */
    public T insert(T entity) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(getInsertSql(), Statement.RETURN_GENERATED_KEYS)) {
            
            setInsertParameters(stmt, entity);
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new DatabaseException("Failed to insert " + getTableName() + ", no rows affected");
            }
            
            // Set the generated ID if applicable
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    setGeneratedId(entity, generatedKeys.getLong(1));
                }
            }
            
            logger.info("Successfully inserted {} with id: {}", getTableName(), getId(entity));
            return entity;
            
        } catch (SQLException e) {
            logger.error("Error inserting {}: {}", getTableName(), entity, e);
            throw new DatabaseException("Failed to insert " + getTableName(), e);
        }
    }
    
    /**
     * Update an existing entity
     */
    public T update(T entity) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(getUpdateSql())) {
            
            setUpdateParameters(stmt, entity);
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new DatabaseException("Failed to update " + getTableName() + ", entity not found");
            }
            
            logger.info("Successfully updated {} with id: {}", getTableName(), getId(entity));
            return entity;
            
        } catch (SQLException e) {
            logger.error("Error updating {}: {}", getTableName(), entity, e);
            throw new DatabaseException("Failed to update " + getTableName(), e);
        }
    }
    
    /**
     * Delete an entity by ID
     */
    public boolean deleteById(ID id) {
        String sql = "DELETE FROM " + getTableName() + " WHERE " + getIdColumnName() + " = ?";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setObject(1, id);
            
            int affectedRows = stmt.executeUpdate();
            boolean deleted = affectedRows > 0;
            
            if (deleted) {
                logger.info("Successfully deleted {} with id: {}", getTableName(), id);
            } else {
                logger.warn("No {} found with id: {} to delete", getTableName(), id);
            }
            
            return deleted;
            
        } catch (SQLException e) {
            logger.error("Error deleting {} by id: {}", getTableName(), id, e);
            throw new DatabaseException("Failed to delete " + getTableName() + " by id: " + id, e);
        }
    }
    
    /**
     * Check if an entity exists by ID
     */
    public boolean existsById(ID id) {
        String sql = "SELECT 1 FROM " + getTableName() + " WHERE " + getIdColumnName() + " = ?";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setObject(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
            
        } catch (SQLException e) {
            logger.error("Error checking existence of {} by id: {}", getTableName(), id, e);
            throw new DatabaseException("Failed to check existence of " + getTableName() + " by id: " + id, e);
        }
    }
    
    /**
     * Count total number of entities
     */
    public long count() {
        String sql = "SELECT COUNT(*) FROM " + getTableName();
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                return rs.getLong(1);
            }
            return 0;
            
        } catch (SQLException e) {
            logger.error("Error counting {}", getTableName(), e);
            throw new DatabaseException("Failed to count " + getTableName(), e);
        }
    }
    
    // Utility methods that may be overridden by subclasses
    
    /**
     * Check if the entity has an ID (for determining insert vs update)
     */
    protected abstract boolean hasId(T entity);
    
    /**
     * Get the ID from the entity
     */
    protected abstract ID getId(T entity);
    
    /**
     * Set the generated ID on the entity
     */
    protected abstract void setGeneratedId(T entity, Long id);
    
    /**
     * Execute a query with parameters and return results
     */
    protected List<T> executeQuery(String sql, Object... parameters) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            // Set parameters
            for (int i = 0; i < parameters.length; i++) {
                stmt.setObject(i + 1, parameters[i]);
            }
            
            try (ResultSet rs = stmt.executeQuery()) {
                List<T> results = new ArrayList<>();
                while (rs.next()) {
                    results.add(mapResultSetToEntity(rs));
                }
                return results;
            }
            
        } catch (SQLException e) {
            logger.error("Error executing query: {}", sql, e);
            throw new DatabaseException("Failed to execute query: " + sql, e);
        }
    }
    
    /**
     * Execute a query and return a single result
     */
    protected Optional<T> executeQueryForSingleResult(String sql, Object... parameters) {
        List<T> results = executeQuery(sql, parameters);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }
    
    /**
     * Execute an update/delete statement
     */
    protected int executeUpdate(String sql, Object... parameters) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            // Set parameters
            for (int i = 0; i < parameters.length; i++) {
                stmt.setObject(i + 1, parameters[i]);
            }
            
            return stmt.executeUpdate();
            
        } catch (SQLException e) {
            logger.error("Error executing update: {}", sql, e);
            throw new DatabaseException("Failed to execute update: " + sql, e);
        }
    }
    
    /**
     * Begin a transaction and return the connection
     * Must be used with try-with-resources and connection.commit()/rollback()
     */
    protected Connection beginTransaction() throws SQLException {
        Connection conn = dataSource.getConnection();
        conn.setAutoCommit(false);
        return conn;
    }
}