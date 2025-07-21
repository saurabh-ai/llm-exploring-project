package com.javamastery.inventory.service;

import com.javamastery.inventory.dao.UserDao;
import com.javamastery.inventory.exception.DatabaseException;
import com.javamastery.inventory.exception.ValidationException;
import com.javamastery.inventory.model.User;
import com.javamastery.inventory.model.UserRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;

/**
 * Service class for User business logic and operations
 */
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    
    private final UserDao userDao;
    
    public UserService(DataSource dataSource) {
        this.userDao = new UserDao(dataSource);
    }
    
    /**
     * Get all users
     */
    public List<User> getAllUsers() throws DatabaseException {
        logger.debug("Getting all users");
        return userDao.findAll();
    }
    
    /**
     * Get user by ID
     */
    public Optional<User> getUserById(Long userId) throws DatabaseException {
        logger.debug("Getting user by ID: {}", userId);
        return userDao.findById(userId);
    }
    
    /**
     * Get user by username
     */
    public Optional<User> getUserByUsername(String username) throws DatabaseException {
        logger.debug("Getting user by username: {}", username);
        return userDao.findByUsername(username);
    }
    
    /**
     * Get users by role
     */
    public List<User> getUsersByRole(UserRole role) throws DatabaseException {
        logger.debug("Getting users by role: {}", role);
        return userDao.findByRole(role);
    }
    
    /**
     * Create new user
     */
    public User createUser(String username, String email, UserRole role) throws DatabaseException, ValidationException {
        logger.info("Creating new user: {}", username);
        
        // Validate input
        validateUserInput(username, email, role);
        
        // Check if username already exists
        Optional<User> existing = userDao.findByUsername(username);
        if (existing.isPresent()) {
            throw new ValidationException("Username already exists: " + username);
        }
        
        User user = new User(username, email, role);
        User saved = userDao.save(user);
        
        logger.info("Successfully created user: {} (ID: {})", username, saved.getUserId());
        return saved;
    }
    
    /**
     * Update existing user
     */
    public User updateUser(User user) throws DatabaseException, ValidationException {
        logger.info("Updating user: {}", user.getUserId());
        
        // Validate input
        validateUserInput(user.getUsername(), user.getEmail(), user.getRole());
        
        // Check if user exists
        Optional<User> existing = userDao.findById(user.getUserId());
        if (existing.isEmpty()) {
            throw new ValidationException("User not found: " + user.getUserId());
        }
        
        // Check if username is being changed to one that already exists
        Optional<User> existingByUsername = userDao.findByUsername(user.getUsername());
        if (existingByUsername.isPresent() && !existingByUsername.get().getUserId().equals(user.getUserId())) {
            throw new ValidationException("Username already exists: " + user.getUsername());
        }
        
        User updated = userDao.save(user);
        logger.info("Successfully updated user: {}", user.getUserId());
        return updated;
    }
    
    /**
     * Delete user by ID
     */
    public boolean deleteUser(Long userId) throws DatabaseException, ValidationException {
        logger.info("Deleting user: {}", userId);
        
        // Check if user exists
        Optional<User> existing = userDao.findById(userId);
        if (existing.isEmpty()) {
            throw new ValidationException("User not found: " + userId);
        }
        
        // Check if user is admin (prevent deleting all admins)
        if (existing.get().getRole() == UserRole.ADMIN) {
            List<User> admins = userDao.findByRole(UserRole.ADMIN);
            if (admins.size() <= 1) {
                throw new ValidationException("Cannot delete the last administrator");
            }
        }
        
        boolean deleted = userDao.deleteById(userId);
        if (deleted) {
            logger.info("Successfully deleted user: {}", userId);
        }
        return deleted;
    }
    
    /**
     * Get total user count
     */
    public long getUserCount() throws DatabaseException {
        List<User> users = userDao.findAll();
        return users.size();
    }
    
    /**
     * Get user count by role
     */
    public long getUserCountByRole(UserRole role) throws DatabaseException {
        List<User> users = userDao.findByRole(role);
        return users.size();
    }
    
    /**
     * Validate user input data
     */
    private void validateUserInput(String username, String email, UserRole role) throws ValidationException {
        if (username == null || username.trim().isEmpty()) {
            throw new ValidationException("Username is required");
        }
        
        if (username.length() < 3) {
            throw new ValidationException("Username must be at least 3 characters long");
        }
        
        if (username.length() > 100) {
            throw new ValidationException("Username cannot be longer than 100 characters");
        }
        
        if (email == null || email.trim().isEmpty()) {
            throw new ValidationException("Email is required");
        }
        
        if (!isValidEmail(email)) {
            throw new ValidationException("Invalid email format");
        }
        
        if (email.length() > 255) {
            throw new ValidationException("Email cannot be longer than 255 characters");
        }
        
        if (role == null) {
            throw new ValidationException("Role is required");
        }
    }
    
    /**
     * Basic email validation
     */
    private boolean isValidEmail(String email) {
        return email.contains("@") && email.contains(".") && email.length() > 5;
    }
}