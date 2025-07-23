package com.javamastery.ecommerce.product.service;

import com.javamastery.ecommerce.shared.exception.BadRequestException;
import com.javamastery.ecommerce.shared.exception.ResourceNotFoundException;
import com.javamastery.ecommerce.product.dto.CategoryDto;
import com.javamastery.ecommerce.product.entity.Category;
import com.javamastery.ecommerce.product.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CategoryService {
    
    @Autowired
    private CategoryRepository categoryRepository;
    
    public List<CategoryDto> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    public CategoryDto getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));
        return convertToDto(category);
    }
    
    public CategoryDto createCategory(CategoryDto categoryDto) {
        if (categoryRepository.existsByName(categoryDto.getName())) {
            throw new BadRequestException("Category with name '" + categoryDto.getName() + "' already exists");
        }
        
        Category category = new Category();
        category.setName(categoryDto.getName());
        category.setDescription(categoryDto.getDescription());
        
        Category savedCategory = categoryRepository.save(category);
        return convertToDto(savedCategory);
    }
    
    public CategoryDto updateCategory(Long id, CategoryDto categoryDto) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));
        
        // Check if name is being changed and if it already exists
        if (!category.getName().equals(categoryDto.getName()) && 
            categoryRepository.existsByName(categoryDto.getName())) {
            throw new BadRequestException("Category with name '" + categoryDto.getName() + "' already exists");
        }
        
        category.setName(categoryDto.getName());
        category.setDescription(categoryDto.getDescription());
        
        Category updatedCategory = categoryRepository.save(category);
        return convertToDto(updatedCategory);
    }
    
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));
        
        categoryRepository.delete(category);
    }
    
    private CategoryDto convertToDto(Category category) {
        CategoryDto dto = new CategoryDto();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setDescription(category.getDescription());
        return dto;
    }
}