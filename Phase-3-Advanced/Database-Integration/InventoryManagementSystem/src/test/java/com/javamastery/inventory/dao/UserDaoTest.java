package com.javamastery.inventory.dao;

import com.javamastery.inventory.config.DatabaseConfig;
import com.javamastery.inventory.config.DatabaseInitializer;
import com.javamastery.inventory.exception.DatabaseException;
import com.javamastery.inventory.model.User;
import com.javamastery.inventory.model.UserRole;
import org.junit.jupiter.api.*;

import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for UserDao
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserDaoTest {
    
    private static DataSource dataSource;
    private static UserDao userDao;
    
    @BeforeAll
    static void setUp() throws Exception {
        // Configure test database with unique name
        System.setProperty("database.url", "jdbc:h2:mem:usertest" + System.currentTimeMillis() + ";DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE");
        
        DatabaseConfig dbConfig = DatabaseConfig.getInstance();
        dataSource = dbConfig.getDataSource();
        userDao = new UserDao(dataSource);
        
        // Initialize database with schema and sample data
        DatabaseInitializer initializer = new DatabaseInitializer(dataSource, dbConfig.getDatabaseType());
        initializer.initializeWithSampleData();
    }
    
    @Test
    @Order(1)
    void testFindAll() throws DatabaseException {
        List<User> users = userDao.findAll();
        
        assertNotNull(users);
        assertTrue(users.size() >= 4); // Should have at least the sample users
        
        // Check that users are ordered by username
        for (int i = 1; i < users.size(); i++) {
            assertTrue(users.get(i-1).getUsername().compareTo(users.get(i).getUsername()) <= 0);
        }
    }
    
    @Test
    @Order(2)
    void testFindById() throws DatabaseException {
        // Find existing user
        Optional<User> user = userDao.findById(1L);
        
        assertTrue(user.isPresent());
        assertEquals("admin", user.get().getUsername());
        assertEquals("admin@inventory.com", user.get().getEmail());
        assertEquals(UserRole.ADMIN, user.get().getRole());
        
        // Find non-existing user
        Optional<User> nonExisting = userDao.findById(999L);
        assertFalse(nonExisting.isPresent());
    }
    
    @Test
    @Order(3)
    void testFindByUsername() throws DatabaseException {
        // Find existing user
        Optional<User> user = userDao.findByUsername("manager1");
        
        assertTrue(user.isPresent());
        assertEquals("manager1@inventory.com", user.get().getEmail());
        assertEquals(UserRole.MANAGER, user.get().getRole());
        
        // Find non-existing user
        Optional<User> nonExisting = userDao.findByUsername("nonexistent");
        assertFalse(nonExisting.isPresent());
    }
    
    @Test
    @Order(4)
    void testFindByRole() throws DatabaseException {
        List<User> admins = userDao.findByRole(UserRole.ADMIN);
        List<User> managers = userDao.findByRole(UserRole.MANAGER);
        List<User> employees = userDao.findByRole(UserRole.EMPLOYEE);
        
        assertTrue(admins.size() >= 1);
        assertTrue(managers.size() >= 1);
        assertTrue(employees.size() >= 2);
        
        // Verify all users have the correct role
        admins.forEach(user -> assertEquals(UserRole.ADMIN, user.getRole()));
        managers.forEach(user -> assertEquals(UserRole.MANAGER, user.getRole()));
        employees.forEach(user -> assertEquals(UserRole.EMPLOYEE, user.getRole()));
    }
    
    @Test
    @Order(5)
    void testSaveNewUser() throws DatabaseException {
        User newUser = new User("testuser", "test@inventory.com", UserRole.EMPLOYEE);
        
        User savedUser = userDao.save(newUser);
        
        assertNotNull(savedUser.getUserId());
        assertEquals("testuser", savedUser.getUsername());
        assertEquals("test@inventory.com", savedUser.getEmail());
        assertEquals(UserRole.EMPLOYEE, savedUser.getRole());
        assertNotNull(savedUser.getCreatedAt());
        assertNotNull(savedUser.getUpdatedAt());
    }
    
    @Test
    @Order(6)
    void testSaveExistingUser() throws DatabaseException {
        // First, get a user to update
        Optional<User> existingUser = userDao.findByUsername("testuser");
        assertTrue(existingUser.isPresent());
        
        User user = existingUser.get();
        user.setEmail("updated@inventory.com");
        user.setRole(UserRole.MANAGER);
        
        User updatedUser = userDao.save(user);
        
        assertEquals(user.getUserId(), updatedUser.getUserId());
        assertEquals("testuser", updatedUser.getUsername());
        assertEquals("updated@inventory.com", updatedUser.getEmail());
        assertEquals(UserRole.MANAGER, updatedUser.getRole());
    }
    
    @Test
    @Order(7)
    void testDeleteById() throws DatabaseException {
        // First, create a user to delete
        User userToDelete = new User("deletetest", "delete@inventory.com", UserRole.EMPLOYEE);
        User savedUser = userDao.save(userToDelete);
        Long userIdToDelete = savedUser.getUserId();
        
        // Verify user exists
        assertTrue(userDao.findById(userIdToDelete).isPresent());
        
        // Delete user
        boolean deleted = userDao.deleteById(userIdToDelete);
        assertTrue(deleted);
        
        // Verify user no longer exists
        assertFalse(userDao.findById(userIdToDelete).isPresent());
        
        // Try to delete non-existing user
        boolean nonExistingDeleted = userDao.deleteById(999L);
        assertFalse(nonExistingDeleted);
    }
    
    @Test
    @Order(8)
    void testUniqueConstraints() {
        // Test duplicate username
        assertThrows(DatabaseException.class, () -> {
            User duplicateUsername = new User("admin", "another@inventory.com", UserRole.EMPLOYEE);
            userDao.save(duplicateUsername);
        });
        
        // Test duplicate email
        assertThrows(DatabaseException.class, () -> {
            User duplicateEmail = new User("another", "admin@inventory.com", UserRole.EMPLOYEE);
            userDao.save(duplicateEmail);
        });
    }
    
    @AfterAll
    static void tearDown() {
        // Clean up if needed
        if (dataSource != null) {
            try {
                dataSource.getConnection().close();
            } catch (Exception e) {
                // Ignore cleanup errors
            }
        }
    }
}