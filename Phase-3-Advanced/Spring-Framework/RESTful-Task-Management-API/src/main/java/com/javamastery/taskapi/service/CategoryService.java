package com.javamastery.taskapi.service;

import com.javamastery.taskapi.dto.CategoryDto;
import com.javamastery.taskapi.dto.CreateCategoryRequest;
import com.javamastery.taskapi.exception.CategoryNotFoundException;
import com.javamastery.taskapi.exception.ValidationException;
import com.javamastery.taskapi.model.Category;
import com.javamastery.taskapi.repository.CategoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class for Category operations
 */
@Service
@Transactional
public class CategoryService {
    
    private static final Logger logger = LoggerFactory.getLogger(CategoryService.class);
    
    private final CategoryRepository categoryRepository;
    
    @Autowired
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }
    
    /**
     * Get all categories with pagination
     */
    @Transactional(readOnly = true)
    public Page<CategoryDto> getAllCategories(Pageable pageable) {
        logger.debug("Fetching all categories with pagination: {}", pageable);
        Page<Category> categories = categoryRepository.findAll(pageable);
        return categories.map(this::convertToDto);
    }
    
    /**
     * Get all categories with task counts
     */
    @Transactional(readOnly = true)
    public List<CategoryDto> getAllCategoriesWithTaskCount() {
        logger.debug("Fetching all categories with task counts");
        List<Object[]> categoriesWithCount = categoryRepository.findAllWithTaskCount();
        
        return categoriesWithCount.stream()
                .map(result -> {
                    Category category = (Category) result[0];
                    Long taskCount = (Long) result[1];
                    CategoryDto dto = convertToDto(category);
                    dto.setTaskCount(taskCount.intValue());
                    return dto;
                })
                .collect(Collectors.toList());
    }
    
    /**
     * Get category by ID
     */
    @Transactional(readOnly = true)
    public CategoryDto getCategoryById(Long id) {
        logger.debug("Fetching category with ID: {}", id);
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(id));
        
        CategoryDto categoryDto = convertToDto(category);
        categoryDto.setTaskCount(category.getTasks().size());
        return categoryDto;
    }
    
    /**
     * Create a new category
     */
    public CategoryDto createCategory(CreateCategoryRequest request) {
        logger.debug("Creating new category: {}", request);
        
        // Validate name uniqueness
        if (categoryRepository.existsByName(request.getName())) {
            throw new ValidationException("Category name already exists: " + request.getName());
        }
        
        Category category = new Category(
                request.getName(),
                request.getDescription(),
                request.getColorCode()
        );
        
        Category savedCategory = categoryRepository.save(category);
        logger.info("Category created with ID: {}", savedCategory.getId());
        
        return convertToDto(savedCategory);
    }
    
    /**
     * Update category
     */
    public CategoryDto updateCategory(Long id, CreateCategoryRequest request) {
        logger.debug("Updating category with ID: {}", id);
        
        Category existingCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(id));
        
        // Validate name uniqueness (excluding current category)
        if (!existingCategory.getName().equals(request.getName()) &&
            categoryRepository.existsByName(request.getName())) {
            throw new ValidationException("Category name already exists: " + request.getName());
        }
        
        existingCategory.setName(request.getName());
        existingCategory.setDescription(request.getDescription());
        existingCategory.setColorCode(request.getColorCode());
        
        Category updatedCategory = categoryRepository.save(existingCategory);
        logger.info("Category updated with ID: {}", updatedCategory.getId());
        
        return convertToDto(updatedCategory);
    }
    
    /**
     * Delete category
     */
    public void deleteCategory(Long id) {
        logger.debug("Deleting category with ID: {}", id);
        
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(id));
        
        // Check if category has tasks
        if (!category.getTasks().isEmpty()) {
            throw new ValidationException("Cannot delete category with existing tasks. Category has " + 
                    category.getTasks().size() + " tasks.");
        }
        
        categoryRepository.delete(category);
        logger.info("Category deleted with ID: {}", id);
    }
    
    /**
     * Search categories by name
     */
    @Transactional(readOnly = true)
    public List<CategoryDto> searchCategoriesByName(String name) {
        logger.debug("Searching categories by name: {}", name);
        List<Category> categories = categoryRepository.findByNameContainingIgnoreCase(name);
        return categories.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Check if category exists
     */
    @Transactional(readOnly = true)
    public boolean categoryExists(Long id) {
        return categoryRepository.existsById(id);
    }
    
    /**
     * Convert Category entity to CategoryDto
     */
    private CategoryDto convertToDto(Category category) {
        CategoryDto dto = new CategoryDto(
                category.getId(),
                category.getName(),
                category.getDescription(),
                category.getColorCode()
        );
        dto.setCreatedAt(category.getCreatedAt());
        dto.setUpdatedAt(category.getUpdatedAt());
        dto.setTaskCount(category.getTasks().size());
        return dto;
    }
}