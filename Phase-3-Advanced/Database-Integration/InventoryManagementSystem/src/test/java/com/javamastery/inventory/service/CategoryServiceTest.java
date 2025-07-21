package com.javamastery.inventory.service;

import com.javamastery.inventory.dao.CategoryDao;
import com.javamastery.inventory.exception.ValidationException;
import com.javamastery.inventory.model.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for CategoryService
 */
@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryDao categoryDao;
    
    private CategoryService categoryService;
    
    @BeforeEach
    void setUp() {
        categoryService = new CategoryService(categoryDao);
    }
    
    @Test
    void testGetAllCategories() {
        // Given
        List<Category> expectedCategories = Arrays.asList(
            createCategory(1L, "Electronics", "Electronic items"),
            createCategory(2L, "Books", "Books and literature")
        );
        when(categoryDao.findAll()).thenReturn(expectedCategories);
        
        // When
        List<Category> result = categoryService.getAllCategories();
        
        // Then
        assertEquals(2, result.size());
        assertEquals("Electronics", result.get(0).getName());
        assertEquals("Books", result.get(1).getName());
        verify(categoryDao).findAll();
    }
    
    @Test
    void testGetCategoryById_Found() {
        // Given
        Long categoryId = 1L;
        Category expectedCategory = createCategory(categoryId, "Electronics", "Electronic items");
        when(categoryDao.findById(categoryId)).thenReturn(Optional.of(expectedCategory));
        
        // When
        Optional<Category> result = categoryService.getCategoryById(categoryId);
        
        // Then
        assertTrue(result.isPresent());
        assertEquals("Electronics", result.get().getName());
        verify(categoryDao).findById(categoryId);
    }
    
    @Test
    void testGetCategoryById_NotFound() {
        // Given
        Long categoryId = 999L;
        when(categoryDao.findById(categoryId)).thenReturn(Optional.empty());
        
        // When
        Optional<Category> result = categoryService.getCategoryById(categoryId);
        
        // Then
        assertFalse(result.isPresent());
        verify(categoryDao).findById(categoryId);
    }
    
    @Test
    void testCreateCategory_ValidData() {
        // Given
        String name = "Test Category";
        String description = "Test Description";
        Category savedCategory = createCategory(1L, name, description);
        
        when(categoryDao.findByName(name)).thenReturn(Optional.empty());
        when(categoryDao.insert(any(Category.class))).thenReturn(savedCategory);
        
        // When
        Category result = categoryService.createCategory(name, description);
        
        // Then
        assertNotNull(result);
        assertEquals(name, result.getName());
        assertEquals(description, result.getDescription());
        verify(categoryDao).findByName(name);
        verify(categoryDao).insert(any(Category.class));
    }
    
    @Test
    void testCreateCategory_DuplicateName() {
        // Given
        String name = "Existing Category";
        String description = "Test Description";
        Category existingCategory = createCategory(1L, name, "Existing description");
        
        when(categoryDao.findByName(name)).thenReturn(Optional.of(existingCategory));
        
        // When & Then
        ValidationException exception = assertThrows(ValidationException.class, 
            () -> categoryService.createCategory(name, description));
        
        assertEquals("Category with name 'Existing Category' already exists", exception.getMessage());
        verify(categoryDao).findByName(name);
        verify(categoryDao, never()).insert(any(Category.class));
    }
    
    @Test
    void testCreateCategory_EmptyName() {
        // When & Then
        ValidationException exception = assertThrows(ValidationException.class, 
            () -> categoryService.createCategory("", "Description"));
        
        assertEquals("Category name is required and cannot be empty", exception.getMessage());
        verifyNoInteractions(categoryDao);
    }
    
    @Test
    void testCreateCategory_NullName() {
        // When & Then
        ValidationException exception = assertThrows(ValidationException.class, 
            () -> categoryService.createCategory(null, "Description"));
        
        assertEquals("Category name is required and cannot be empty", exception.getMessage());
        verifyNoInteractions(categoryDao);
    }
    
    @Test
    void testCreateCategory_TooLongName() {
        // Given
        String longName = "x".repeat(256); // Assuming 255 is the limit
        
        // When & Then
        ValidationException exception = assertThrows(ValidationException.class, 
            () -> categoryService.createCategory(longName, "Description"));
        
        assertEquals("Category name cannot exceed 255 characters", exception.getMessage());
        verifyNoInteractions(categoryDao);
    }
    
    @Test
    void testUpdateCategory_ValidData() {
        // Given
        Long categoryId = 1L;
        String newName = "Updated Category";
        String newDescription = "Updated Description";
        Category existingCategory = createCategory(categoryId, "Old Name", "Old Description");
        Category updatedCategory = createCategory(categoryId, newName, newDescription);
        
        when(categoryDao.findById(categoryId)).thenReturn(Optional.of(existingCategory));
        when(categoryDao.findByName(newName)).thenReturn(Optional.empty());
        when(categoryDao.update(any(Category.class))).thenReturn(updatedCategory);
        
        // When
        Category result = categoryService.updateCategory(categoryId, newName, newDescription);
        
        // Then
        assertEquals(newName, result.getName());
        assertEquals(newDescription, result.getDescription());
        verify(categoryDao).findById(categoryId);
        verify(categoryDao).findByName(newName);
        verify(categoryDao).update(any(Category.class));
    }
    
    @Test
    void testUpdateCategory_NotFound() {
        // Given
        Long categoryId = 999L;
        when(categoryDao.findById(categoryId)).thenReturn(Optional.empty());
        
        // When & Then
        ValidationException exception = assertThrows(ValidationException.class, 
            () -> categoryService.updateCategory(categoryId, "New Name", "New Description"));
        
        assertEquals("Category with ID 999 not found", exception.getMessage());
        verify(categoryDao).findById(categoryId);
        verify(categoryDao, never()).update(any(Category.class));
    }
    
    @Test
    void testDeleteCategory_Success() {
        // Given
        Long categoryId = 1L;
        Category existingCategory = createCategory(categoryId, "Test Category", "Description");
        
        when(categoryDao.findById(categoryId)).thenReturn(Optional.of(existingCategory));
        when(categoryDao.hasProducts(categoryId)).thenReturn(false);
        doNothing().when(categoryDao).deleteById(categoryId);
        
        // When
        boolean result = categoryService.deleteCategory(categoryId);
        
        // Then
        assertTrue(result);
        verify(categoryDao).findById(categoryId);
        verify(categoryDao).hasProducts(categoryId);
        verify(categoryDao).deleteById(categoryId);
    }
    
    @Test
    void testDeleteCategory_HasProducts() {
        // Given
        Long categoryId = 1L;
        Category existingCategory = createCategory(categoryId, "Test Category", "Description");
        
        when(categoryDao.findById(categoryId)).thenReturn(Optional.of(existingCategory));
        when(categoryDao.hasProducts(categoryId)).thenReturn(true);
        
        // When & Then
        ValidationException exception = assertThrows(ValidationException.class, 
            () -> categoryService.deleteCategory(categoryId));
        
        assertEquals("Cannot delete category 'Test Category' as it contains products", exception.getMessage());
        verify(categoryDao).findById(categoryId);
        verify(categoryDao).hasProducts(categoryId);
        verify(categoryDao, never()).deleteById(categoryId);
    }
    
    @Test
    void testSearchCategories() {
        // Given
        String searchTerm = "elec";
        List<Category> expectedResults = Arrays.asList(
            createCategory(1L, "Electronics", "Electronic items")
        );
        when(categoryDao.searchByName(searchTerm)).thenReturn(expectedResults);
        
        // When
        List<Category> results = categoryService.searchCategories(searchTerm);
        
        // Then
        assertEquals(1, results.size());
        assertEquals("Electronics", results.get(0).getName());
        verify(categoryDao).searchByName(searchTerm);
    }
    
    private Category createCategory(Long id, String name, String description) {
        Category category = new Category(name, description);
        category.setCategoryId(id);
        return category;
    }
}