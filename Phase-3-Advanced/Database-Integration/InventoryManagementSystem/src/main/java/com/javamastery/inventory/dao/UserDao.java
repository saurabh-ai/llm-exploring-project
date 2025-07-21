package com.javamastery.inventory.dao;

import com.javamastery.inventory.exception.DatabaseException;
import com.javamastery.inventory.model.User;
import com.javamastery.inventory.model.UserRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object for User entities
 */
public class UserDao {
    private static final Logger logger = LoggerFactory.getLogger(UserDao.class);
    protected final DataSource dataSource;
    
    public UserDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    
    /**
     * Find all users
     */
    public List<User> findAll() throws DatabaseException {
        String sql = """
            SELECT user_id, username, email, role, created_at, updated_at
            FROM users
            ORDER BY username
            """;
        
        List<User> users = new ArrayList<>();
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
            
            logger.debug("Found {} users", users.size());
            return users;
            
        } catch (SQLException e) {
            logger.error("Error finding all users", e);
            throw new DatabaseException("Failed to retrieve users", e);
        }
    }
    
    /**
     * Find user by ID
     */
    public Optional<User> findById(Long userId) throws DatabaseException {
        String sql = """
            SELECT user_id, username, email, role, created_at, updated_at
            FROM users
            WHERE user_id = ?
            """;
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, userId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToUser(rs));
                }
                return Optional.empty();
            }
            
        } catch (SQLException e) {
            logger.error("Error finding user by ID: {}", userId, e);
            throw new DatabaseException("Failed to find user by ID", e);
        }
    }
    
    /**
     * Find user by username
     */
    public Optional<User> findByUsername(String username) throws DatabaseException {
        String sql = """
            SELECT user_id, username, email, role, created_at, updated_at
            FROM users
            WHERE username = ?
            """;
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, username);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToUser(rs));
                }
                return Optional.empty();
            }
            
        } catch (SQLException e) {
            logger.error("Error finding user by username: {}", username, e);
            throw new DatabaseException("Failed to find user by username", e);
        }
    }
    
    /**
     * Find users by role
     */
    public List<User> findByRole(UserRole role) throws DatabaseException {
        String sql = """
            SELECT user_id, username, email, role, created_at, updated_at
            FROM users
            WHERE role = ?
            ORDER BY username
            """;
        
        List<User> users = new ArrayList<>();
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, role.name());
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    users.add(mapResultSetToUser(rs));
                }
            }
            
            logger.debug("Found {} users with role {}", users.size(), role);
            return users;
            
        } catch (SQLException e) {
            logger.error("Error finding users by role: {}", role, e);
            throw new DatabaseException("Failed to find users by role", e);
        }
    }
    
    /**
     * Create a new user
     */
    public User save(User user) throws DatabaseException {
        if (user.getUserId() == null) {
            return insert(user);
        } else {
            return update(user);
        }
    }
    
    /**
     * Insert new user
     */
    private User insert(User user) throws DatabaseException {
        String sql = """
            INSERT INTO users (username, email, role)
            VALUES (?, ?, ?)
            """;
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getRole().name());
            
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new DatabaseException("Failed to insert user, no rows affected");
            }
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    Long userId = generatedKeys.getLong(1);
                    user.setUserId(userId);
                    logger.info("Successfully inserted user with id: {}", userId);
                    return findById(userId).orElse(user);
                } else {
                    throw new DatabaseException("Failed to get generated key for user");
                }
            }
            
        } catch (SQLException e) {
            logger.error("Error inserting user: {}", user.getUsername(), e);
            throw new DatabaseException("Failed to insert user", e);
        }
    }
    
    /**
     * Update existing user
     */
    private User update(User user) throws DatabaseException {
        String sql = """
            UPDATE users
            SET username = ?, email = ?, role = ?, updated_at = CURRENT_TIMESTAMP
            WHERE user_id = ?
            """;
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getRole().name());
            stmt.setLong(4, user.getUserId());
            
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new DatabaseException("Failed to update user, user not found: " + user.getUserId());
            }
            
            logger.info("Successfully updated user with id: {}", user.getUserId());
            return findById(user.getUserId()).orElse(user);
            
        } catch (SQLException e) {
            logger.error("Error updating user: {}", user.getUserId(), e);
            throw new DatabaseException("Failed to update user", e);
        }
    }
    
    /**
     * Delete user by ID
     */
    public boolean deleteById(Long userId) throws DatabaseException {
        String sql = "DELETE FROM users WHERE user_id = ?";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, userId);
            
            int rowsAffected = stmt.executeUpdate();
            boolean deleted = rowsAffected > 0;
            
            if (deleted) {
                logger.info("Successfully deleted user with id: {}", userId);
            } else {
                logger.warn("No user found with id: {}", userId);
            }
            
            return deleted;
            
        } catch (SQLException e) {
            logger.error("Error deleting user: {}", userId, e);
            throw new DatabaseException("Failed to delete user", e);
        }
    }
    
    /**
     * Map ResultSet to User object
     */
    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        Long userId = rs.getLong("user_id");
        String username = rs.getString("username");
        String email = rs.getString("email");
        UserRole role = UserRole.valueOf(rs.getString("role"));
        
        Timestamp createdTimestamp = rs.getTimestamp("created_at");
        LocalDateTime createdAt = createdTimestamp != null ? createdTimestamp.toLocalDateTime() : null;
        
        Timestamp updatedTimestamp = rs.getTimestamp("updated_at");
        LocalDateTime updatedAt = updatedTimestamp != null ? updatedTimestamp.toLocalDateTime() : null;
        
        return new User(userId, username, email, role, createdAt, updatedAt);
    }
}