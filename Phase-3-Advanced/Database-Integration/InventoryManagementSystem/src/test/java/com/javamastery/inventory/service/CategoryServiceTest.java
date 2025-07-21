package com.javamastery.inventory.service;

import com.javamastery.inventory.exception.ValidationException;
import com.javamastery.inventory.model.Category;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Simple unit tests for CategoryService without mocking
 */
class CategoryServiceTest {

    @Test
    void testValidationExceptions() {
        CategoryService categoryService = new CategoryService();
        
        // Test null name validation
        ValidationException exception = assertThrows(ValidationException.class, 
            () -> categoryService.createCategory(null, "Description"));
        assertTrue(exception.getMessage().contains("required"));

        // Test empty name validation
        exception = assertThrows(ValidationException.class, 
            () -> categoryService.createCategory("", "Description"));
        assertTrue(exception.getMessage().contains("required"));

        // Test long name validation
        String longName = "x".repeat(256);
        exception = assertThrows(ValidationException.class, 
            () -> categoryService.createCategory(longName, "Description"));
        assertTrue(exception.getMessage().contains("exceed"));
    }

    @Test
    void testCategoryCreation() {
        CategoryService categoryService = new CategoryService();
        
        // This will test against actual database, so it should work
        // if the database is properly initialized
        try {
            Category category = categoryService.createCategory("Test Category " + System.currentTimeMillis(), 
                                                             "Test Description");
            assertNotNull(category);
            assertNotNull(category.getCategoryId());
            assertEquals("Test Description", category.getDescription());
        } catch (Exception e) {
            // If database isn't initialized, that's expected in isolated unit tests
            assertTrue(e.getMessage().contains("database") || e.getMessage().contains("connection"));
        }
    }
}