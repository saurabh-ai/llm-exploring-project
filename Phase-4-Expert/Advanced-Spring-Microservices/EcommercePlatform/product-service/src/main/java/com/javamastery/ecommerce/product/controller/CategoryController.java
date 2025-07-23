package com.javamastery.ecommerce.product.controller;

import com.javamastery.ecommerce.shared.dto.BaseResponse;
import com.javamastery.ecommerce.product.dto.CategoryDto;
import com.javamastery.ecommerce.product.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@Tag(name = "Category Management", description = "CRUD operations for product categories")
public class CategoryController {
    
    @Autowired
    private CategoryService categoryService;
    
    @GetMapping
    @Operation(summary = "Get all categories")
    public ResponseEntity<BaseResponse<List<CategoryDto>>> getAllCategories() {
        List<CategoryDto> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(BaseResponse.success(categories));
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get category by ID")
    public ResponseEntity<BaseResponse<CategoryDto>> getCategoryById(@PathVariable Long id) {
        CategoryDto category = categoryService.getCategoryById(id);
        return ResponseEntity.ok(BaseResponse.success(category));
    }
    
    @PostMapping
    @Operation(summary = "Create a new category")
    public ResponseEntity<BaseResponse<CategoryDto>> createCategory(
            @Valid @RequestBody CategoryDto categoryDto) {
        
        CategoryDto createdCategory = categoryService.createCategory(categoryDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(BaseResponse.success("Category created successfully", createdCategory));
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update an existing category")
    public ResponseEntity<BaseResponse<CategoryDto>> updateCategory(
            @PathVariable Long id, @Valid @RequestBody CategoryDto categoryDto) {
        
        CategoryDto updatedCategory = categoryService.updateCategory(id, categoryDto);
        return ResponseEntity.ok(BaseResponse.success("Category updated successfully", updatedCategory));
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a category")
    public ResponseEntity<BaseResponse<Void>> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.ok(BaseResponse.success("Category deleted successfully", null));
    }
}