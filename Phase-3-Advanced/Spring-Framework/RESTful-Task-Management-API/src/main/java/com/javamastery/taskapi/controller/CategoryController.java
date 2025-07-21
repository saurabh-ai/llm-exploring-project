package com.javamastery.taskapi.controller;

import com.javamastery.taskapi.dto.*;
import com.javamastery.taskapi.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Category management operations
 */
@RestController
@RequestMapping("/categories")
@Tag(name = "Category Management", description = "Operations related to category management")
public class CategoryController {
    
    private static final Logger logger = LoggerFactory.getLogger(CategoryController.class);
    
    private final CategoryService categoryService;
    
    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }
    
    @Operation(summary = "Get all categories with pagination")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved categories")
    })
    @GetMapping
    public ResponseEntity<com.javamastery.taskapi.dto.ApiResponse<Page<CategoryDto>>> getAllCategories(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "name") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "asc") String sortDir,
            @Parameter(description = "Include task counts") @RequestParam(defaultValue = "false") boolean includeTaskCount) {
        
        logger.info("GET /categories - page: {}, size: {}, sortBy: {}, sortDir: {}, includeTaskCount: {}", 
                   page, size, sortBy, sortDir, includeTaskCount);
        
        if (includeTaskCount) {
            List<CategoryDto> categoriesWithCount = categoryService.getAllCategoriesWithTaskCount();
            // Create a Page from the list for consistent response format
            Page<CategoryDto> categoryPage = new org.springframework.data.domain.PageImpl<>(categoriesWithCount);
            return ResponseEntity.ok(com.javamastery.taskapi.dto.ApiResponse.success(categoryPage, 
                    "Successfully retrieved " + categoriesWithCount.size() + " categories with task counts"));
        } else {
            Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
            Pageable pageable = PageRequest.of(page, size, sort);
            
            Page<CategoryDto> categories = categoryService.getAllCategories(pageable);
            
            return ResponseEntity.ok(com.javamastery.taskapi.dto.ApiResponse.success(categories, 
                    "Successfully retrieved " + categories.getTotalElements() + " categories"));
        }
    }
    
    @Operation(summary = "Get all categories with task counts")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved categories with task counts")
    })
    @GetMapping("/with-counts")
    public ResponseEntity<com.javamastery.taskapi.dto.ApiResponse<List<CategoryDto>>> getAllCategoriesWithCounts() {
        
        logger.info("GET /categories/with-counts");
        
        List<CategoryDto> categories = categoryService.getAllCategoriesWithTaskCount();
        
        return ResponseEntity.ok(com.javamastery.taskapi.dto.ApiResponse.success(categories, 
                "Successfully retrieved " + categories.size() + " categories with task counts"));
    }
    
    @Operation(summary = "Get category by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Category found"),
        @ApiResponse(responseCode = "404", description = "Category not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<com.javamastery.taskapi.dto.ApiResponse<CategoryDto>> getCategoryById(
            @Parameter(description = "Category ID") @PathVariable Long id) {
        
        logger.info("GET /categories/{}", id);
        
        CategoryDto category = categoryService.getCategoryById(id);
        
        return ResponseEntity.ok(com.javamastery.taskapi.dto.ApiResponse.success(category, 
                "Category retrieved successfully"));
    }
    
    @Operation(summary = "Create a new category")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Category created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input or category name already exists")
    })
    @PostMapping
    public ResponseEntity<com.javamastery.taskapi.dto.ApiResponse<CategoryDto>> createCategory(
            @Valid @RequestBody CreateCategoryRequest request) {
        
        logger.info("POST /categories - {}", request);
        
        CategoryDto createdCategory = categoryService.createCategory(request);
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(com.javamastery.taskapi.dto.ApiResponse.success(createdCategory, 
                        "Category created successfully"));
    }
    
    @Operation(summary = "Update an existing category")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Category updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input or category name already exists"),
        @ApiResponse(responseCode = "404", description = "Category not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<com.javamastery.taskapi.dto.ApiResponse<CategoryDto>> updateCategory(
            @Parameter(description = "Category ID") @PathVariable Long id,
            @Valid @RequestBody CreateCategoryRequest request) {
        
        logger.info("PUT /categories/{} - {}", id, request);
        
        CategoryDto updatedCategory = categoryService.updateCategory(id, request);
        
        return ResponseEntity.ok(com.javamastery.taskapi.dto.ApiResponse.success(updatedCategory, 
                "Category updated successfully"));
    }
    
    @Operation(summary = "Delete a category")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Category deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Category not found"),
        @ApiResponse(responseCode = "400", description = "Cannot delete category with existing tasks")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<com.javamastery.taskapi.dto.ApiResponse<String>> deleteCategory(
            @Parameter(description = "Category ID") @PathVariable Long id) {
        
        logger.info("DELETE /categories/{}", id);
        
        categoryService.deleteCategory(id);
        
        return ResponseEntity.ok(com.javamastery.taskapi.dto.ApiResponse.success(
                "Category deleted successfully"));
    }
    
    @Operation(summary = "Search categories by name")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Search completed successfully")
    })
    @GetMapping("/search")
    public ResponseEntity<com.javamastery.taskapi.dto.ApiResponse<List<CategoryDto>>> searchCategories(
            @Parameter(description = "Category name to search") @RequestParam String name) {
        
        logger.info("GET /categories/search?name={}", name);
        
        List<CategoryDto> categories = categoryService.searchCategoriesByName(name);
        
        return ResponseEntity.ok(com.javamastery.taskapi.dto.ApiResponse.success(categories, 
                "Found " + categories.size() + " categories matching '" + name + "'"));
    }
}