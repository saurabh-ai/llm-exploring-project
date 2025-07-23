package com.javamastery.ecommerce.product.service;

import com.javamastery.ecommerce.shared.exception.BadRequestException;
import com.javamastery.ecommerce.shared.exception.ResourceNotFoundException;
import com.javamastery.ecommerce.product.dto.ProductDto;
import com.javamastery.ecommerce.product.entity.Category;
import com.javamastery.ecommerce.product.entity.Product;
import com.javamastery.ecommerce.product.repository.CategoryRepository;
import com.javamastery.ecommerce.product.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProductService {
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private CategoryRepository categoryRepository;
    
    public Page<ProductDto> getAllProducts(Pageable pageable) {
        return productRepository.findByIsActiveTrue(pageable)
                .map(this::convertToDto);
    }
    
    public Page<ProductDto> getProductsByCategory(Long categoryId, Pageable pageable) {
        return productRepository.findByCategoryIdAndIsActiveTrue(categoryId, pageable)
                .map(this::convertToDto);
    }
    
    public Page<ProductDto> searchProducts(String searchTerm, Pageable pageable) {
        return productRepository.searchProducts(searchTerm, pageable)
                .map(this::convertToDto);
    }
    
    public ProductDto getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
        return convertToDto(product);
    }
    
    public ProductDto getProductBySku(String sku) {
        Product product = productRepository.findBySku(sku)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "sku", sku));
        return convertToDto(product);
    }
    
    public ProductDto createProduct(ProductDto productDto) {
        // Check if SKU already exists
        if (productDto.getSku() != null && productRepository.existsBySku(productDto.getSku())) {
            throw new BadRequestException("Product with SKU '" + productDto.getSku() + "' already exists");
        }
        
        Product product = new Product();
        product.setName(productDto.getName());
        product.setDescription(productDto.getDescription());
        product.setPrice(productDto.getPrice());
        product.setStockQuantity(productDto.getStockQuantity());
        product.setSku(productDto.getSku() != null ? productDto.getSku() : generateSku());
        product.setImageUrl(productDto.getImageUrl());
        product.setIsActive(productDto.getIsActive());
        
        // Set category if provided
        if (productDto.getCategoryId() != null) {
            Category category = categoryRepository.findById(productDto.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category", "id", productDto.getCategoryId()));
            product.setCategory(category);
        }
        
        Product savedProduct = productRepository.save(product);
        return convertToDto(savedProduct);
    }
    
    public ProductDto updateProduct(Long id, ProductDto productDto) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
        
        // Check if SKU is being changed and if it already exists
        if (productDto.getSku() != null && 
            !productDto.getSku().equals(product.getSku()) && 
            productRepository.existsBySku(productDto.getSku())) {
            throw new BadRequestException("Product with SKU '" + productDto.getSku() + "' already exists");
        }
        
        product.setName(productDto.getName());
        product.setDescription(productDto.getDescription());
        product.setPrice(productDto.getPrice());
        product.setStockQuantity(productDto.getStockQuantity());
        if (productDto.getSku() != null) {
            product.setSku(productDto.getSku());
        }
        product.setImageUrl(productDto.getImageUrl());
        product.setIsActive(productDto.getIsActive());
        
        // Update category if provided
        if (productDto.getCategoryId() != null) {
            Category category = categoryRepository.findById(productDto.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category", "id", productDto.getCategoryId()));
            product.setCategory(category);
        }
        
        Product updatedProduct = productRepository.save(product);
        return convertToDto(updatedProduct);
    }
    
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
        
        // Soft delete by setting isActive to false
        product.setIsActive(false);
        productRepository.save(product);
    }
    
    public ProductDto updateStock(Long id, Integer newQuantity) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
        
        if (newQuantity < 0) {
            throw new BadRequestException("Stock quantity cannot be negative");
        }
        
        product.setStockQuantity(newQuantity);
        Product updatedProduct = productRepository.save(product);
        return convertToDto(updatedProduct);
    }
    
    public List<ProductDto> getLowStockProducts(Integer threshold) {
        return productRepository.findLowStockProducts(threshold).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    private ProductDto convertToDto(Product product) {
        ProductDto dto = new ProductDto();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setPrice(product.getPrice());
        dto.setStockQuantity(product.getStockQuantity());
        dto.setSku(product.getSku());
        dto.setImageUrl(product.getImageUrl());
        dto.setIsActive(product.getIsActive());
        
        if (product.getCategory() != null) {
            dto.setCategoryId(product.getCategory().getId());
            dto.setCategoryName(product.getCategory().getName());
        }
        
        return dto;
    }
    
    private String generateSku() {
        return "SKU-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}